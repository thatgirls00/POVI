package org.example.povi.domain.community.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.povi.domain.community.dto.request.PostCreateRequest;
import org.example.povi.domain.community.dto.response.PostCreateResponse;
import org.example.povi.domain.community.dto.response.PostDeleteResponse;
import org.example.povi.domain.community.dto.request.PostUpdateRequest;
import org.example.povi.domain.community.dto.response.PostDetailResponse;
import org.example.povi.domain.community.dto.response.PostListResponse;
import org.example.povi.domain.community.dto.response.PostUpdateResponse;
import org.example.povi.domain.community.entity.CommunityImage;
import org.example.povi.domain.community.entity.CommunityPost;
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
            post.getImages().forEach(image -> localFileService.deleteFile(image.getImageUrl()));
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
        // Page 객체의 map 기능을 사용하여 Page<CommunityPost>를 Page<PostSummaryResponse>로 변환
        return posts.map(PostListResponse::from);
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



}
