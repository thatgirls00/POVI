package org.example.povi.domain.community.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.povi.domain.community.dto.request.CommentCreateRequest;
import org.example.povi.domain.community.dto.request.PostCreateRequest;
import org.example.povi.domain.community.dto.response.*;
import org.example.povi.domain.community.dto.request.PostUpdateRequest;
import org.example.povi.domain.community.entity.Comment;
import org.example.povi.domain.community.entity.CommunityImage;
import org.example.povi.domain.community.entity.CommunityPost;
import org.example.povi.domain.community.repository.CommentRepository;
import org.example.povi.domain.community.entity.CommunityBookmark;
import org.example.povi.domain.community.repository.CommunityBookmarkRepository;
import org.example.povi.domain.community.repository.CommunityImageRepository;
import org.example.povi.domain.community.repository.CommunityRepository;
import org.example.povi.domain.user.entity.User;
import org.example.povi.domain.user.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;
    private final CommunityImageRepository communityImageRepository;
    private final UserRepository userRepository;
    private final FileUploadService fileUploadService;
    private final CommentRepository commentRepository;
    private final CommunityBookmarkRepository bookmarkRepository;

    @Transactional
    public PostCreateResponse createPost(Long userId, PostCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        CommunityPost post = request.toEntity(user);
        CommunityPost savedPost = communityRepository.save(post);

        if (request.imageUrls() != null && !request.imageUrls().isEmpty()) {
            List<CommunityImage> images = request.imageUrls().stream()
                    .map(url -> CommunityImage.builder()
                            .imageUrl(url)
                            .communityPost(savedPost) // 저장된 게시글과 연결
                            .build())
                    .collect(Collectors.toList());

            communityImageRepository.saveAll(images); // 이미지 목록 한 번에 저장
        }

        return new PostCreateResponse(savedPost.getId(), "게시글이 성공적으로 생성되었습니다.");

    }

    @Transactional
    public PostDeleteResponse deletePost(Long userId, Long postId) {
        CommunityPost post = communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getUser().getId().equals(userId)) {
            throw new SecurityException("삭제 권한이 없는 사용자입니다.");
        }
        communityRepository.delete(post);

        return new PostDeleteResponse(postId, "게시글이 성공적으로 삭제되었습니다.");
    }


    @Transactional
    public PostUpdateResponse updatePost(Long userId, Long postId, PostUpdateRequest request, List<MultipartFile> images) {

        CommunityPost post = communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (!post.getUser().getId().equals(userId)) {
            throw new SecurityException("수정 권한이 없는 사용자입니다.");
        }

        post.updatePost(request.title(), request.content());
        deleteExistingImages(post);
        List<String> newImageUrls = uploadNewImages(post, images);

        return new PostUpdateResponse(post.getId(), post.getTitle(), post.getContent(), newImageUrls, "게시글이 성공적으로 수정되었습니다.");
    }



    private void deleteExistingImages(CommunityPost post) {
        if (post.getImages() != null && !post.getImages().isEmpty()) {
            post.getImages().forEach(image -> fileUploadService.deleteFile(image.getImageUrl()));
            communityImageRepository.deleteAllByCommunityPost(post);
            post.getImages().clear();
        }
    }

    private List<String> uploadNewImages(CommunityPost post, List<MultipartFile> multipartFiles) {
        if (multipartFiles == null || multipartFiles.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> imageUrls = fileUploadService.uploadFiles(multipartFiles);
        List<CommunityImage> newImages = imageUrls.stream()
                .map(url -> CommunityImage.builder() // Builder 패턴 사용
                        .imageUrl(url)
                        .communityPost(post)
                        .build())
                .collect(Collectors.toList());

        communityImageRepository.saveAll(newImages);
        post.getImages().addAll(newImages);

        return imageUrls;
    }

    @Transactional(readOnly = true)
    public Page<PostListResponse> getPostList(Pageable pageable) {
        Page<CommunityPost> posts = communityRepository.findAll(pageable);
        Page<PostListResponse> dtoPage = posts.map(PostListResponse::from);
        return dtoPage;
    }

    @Transactional(readOnly = true)
    public Page<PostListResponse> getMyPostList(Long userId, Pageable pageable) {
        Page<CommunityPost> posts = communityRepository.findAllByUserId(userId, pageable);
        Page<PostListResponse> dtoPage = posts.map(PostListResponse::from);
        return dtoPage;
    }

    @Transactional(readOnly = true)
    public PostDetailResponse getPostDetail(Long postId) {
        CommunityPost post = communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // TODO: 만약 상세 조회가 조회수 증가를 포함해야 한다면,
        // post.increaseViewCount(); 같은 메서드를 호출하고
        // @Transactional(readOnly = true)에서 readOnly = true를 제거해야 합니다.

        return PostDetailResponse.from(post);
    }

    @Transactional
    public CommentCreateResponse createComment(Long userId, Long postId, CommentCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));
        CommunityPost post = communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + postId));

        Comment comment = Comment.builder()
                .content(request.content())
                .user(user)
                .communityPost(post)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return CommentCreateResponse.from(savedComment);
    }

    @Transactional
    public CommentDeleteResponse deleteComment(Long userId, Long commentId){
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다. ID: " + commentId));

        Long commentAuthorId = comment.getUser().getId();
        Long postAuthorId = comment.getCommunityPost().getUser().getId();

        if (!userId.equals(commentAuthorId) && !userId.equals(postAuthorId)) {
            throw new SecurityException("댓글을 삭제할 권한이 없습니다.");
        }

        commentRepository.delete(comment);
        return new CommentDeleteResponse(commentId, "댓글이 성공적으로 삭제되었습니다.");
    }

    @Transactional
    public LikeResponse addLikeToComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다. ID: " + commentId));
        comment.addLike();
        return new LikeResponse(commentId, comment.getLikeCount());
    }

    @Transactional
    public LikeResponse removeLikeFromComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("댓글을 찾을 수 없습니다. ID: " + commentId));
        comment.removeLike();
        return new LikeResponse(commentId, comment.getLikeCount());
    }

    @Transactional
    public LikeResponse addLikeToPost(Long postId) {
        CommunityPost post = communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + postId));
        post.addLike();
        return new LikeResponse(postId, post.getLikeCount());
    }

    @Transactional
    public LikeResponse removeLikeFromPost(Long postId) {
        CommunityPost post = communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다. ID: " + postId));
        post.removeLike();
        return new LikeResponse(postId, post.getLikeCount());
    }

    @Transactional
    public PostBookmarkResponse addBookmark(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        CommunityPost post = communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        if (bookmarkRepository.existsByUserAndCommunityPost(user, post)) {
            throw new IllegalArgumentException("이미 북마크한 게시글입니다.");
        }

        CommunityBookmark bookmark = CommunityBookmark.builder()
                .user(user)
                .communityPost(post)
                .build();

        bookmarkRepository.save(bookmark);

        return new PostBookmarkResponse(postId, "게시글을 북마크했습니다.");
    }

    @Transactional
    public PostBookmarkResponse removeBookmark(Long userId, Long postId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        CommunityPost post = communityRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        CommunityBookmark bookmark = bookmarkRepository.findByUserAndCommunityPost(user, post)
                .orElseThrow(() -> new IllegalArgumentException("북마크하지 않은 게시글입니다."));

        bookmarkRepository.delete(bookmark);

        return new PostBookmarkResponse(postId, "북마크를 취소했습니다.");
    }

    public Page<BookmarkListResponse> getMyBookmarks(Long userId, Pageable pageable) {
        Page<CommunityPost> bookmarkedPosts = communityRepository.findAllByUserId(userId, pageable);
        Page<BookmarkListResponse> dtoPage = bookmarkedPosts.map(BookmarkListResponse::from);
        return dtoPage;
    }
}
