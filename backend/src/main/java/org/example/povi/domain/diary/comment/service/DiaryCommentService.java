package org.example.povi.domain.diary.comment.service;

import lombok.RequiredArgsConstructor;
import org.example.povi.domain.diary.comment.dto.request.DiaryCommentCreateReq;
import org.example.povi.domain.diary.comment.dto.request.DiaryCommentUpdateReq;
import org.example.povi.domain.diary.comment.dto.response.DiaryCommentCreateRes;
import org.example.povi.domain.diary.comment.dto.response.DiaryCommentRes;
import org.example.povi.domain.diary.comment.dto.response.DiaryCommentUpdateRes;
import org.example.povi.domain.diary.comment.entity.DiaryComment;
import org.example.povi.domain.diary.comment.mapper.DiaryCommentMapper;
import org.example.povi.domain.diary.comment.mapper.DiaryCommentRequestMapper;
import org.example.povi.domain.diary.comment.repository.DiaryCommentRepository;
import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.example.povi.domain.diary.post.repository.DiaryPostRepository;
import org.example.povi.domain.user.entity.User;
import org.example.povi.domain.user.follow.service.FollowService;
import org.example.povi.domain.user.repository.UserRepository;
import org.example.povi.global.dto.PagedResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class DiaryCommentService {

    private final DiaryCommentRepository diaryCommentRepository;
    private final DiaryPostRepository diaryPostRepository;
    private final UserRepository userRepository;
    private final FollowService followService;

    /**
     * 댓글 생성
     */
    @Transactional
    public DiaryCommentCreateRes createDiaryComment(Long postId,
                                                    DiaryCommentCreateReq createReq,
                                                    Long currentUserId) {

        // 댓글 작성자 및 게시글 검증
        User commenter = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자가 존재하지 않습니다."));

        DiaryPost targetPost = diaryPostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."));

        // 접근 권한 확인
        if (!canAccessPost(currentUserId, targetPost)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "댓글을 작성할 권한이 없습니다.");
        }

        DiaryComment toSave = DiaryCommentRequestMapper.fromCreateRequest(createReq, commenter, targetPost);
        DiaryComment saved = diaryCommentRepository.save(toSave);
        return DiaryCommentCreateRes.from(saved);
    }

    /**
     * 댓글 삭제 (댓글 작성자와 게시글 작성자만 삭제 가능)
     */
    @Transactional
    public void deleteDiaryComment(Long postId, Long commentId, Long currentUserId) {

        // 댓글 + 게시글 매칭 검증
        DiaryComment comment = diaryCommentRepository
                .findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "해당 댓글이 존재하지 않거나 게시글과 매칭되지 않습니다."
                ));

        DiaryPost targetPost = comment.getPost();
        Long commentAuthorId = comment.getAuthor().getId();
        Long postAuthorId = targetPost.getUser().getId();

        // 2. 게시글 접근 가능 여부 확인
        if (!canAccessPost(currentUserId, targetPost)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "이 게시글에 접근할 수 없습니다.");
        }

        // 3. 삭제 권한 확인 (댓글 작성자 or 게시글 작성자)
        if (!currentUserId.equals(commentAuthorId) && !currentUserId.equals(postAuthorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "댓글 작성자 또는 게시글 작성자만 삭제할 수 있습니다.");
        }

        // 삭제 수행
        diaryCommentRepository.delete(comment);
    }

    /**
     * 댓글 목록 조회
     */
    @Transactional(readOnly = true)
    public PagedResponse<DiaryCommentRes> getCommentsByPost(Long postId, Pageable pageable, Long currentUserId) {

        DiaryPost post = diaryPostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."));

        if (!canAccessPost(currentUserId, post)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "이 게시글에 접근할 수 없습니다.");
        }

        Page<DiaryComment> commentPage = diaryCommentRepository.findByPostId(postId, pageable);

        return DiaryCommentMapper.toPagedResponse(commentPage);
    }

    /**
     * 댓글 수정
     */
    @Transactional
    public DiaryCommentUpdateRes updateDiaryComment(Long postId,
                                                    Long commentId,
                                                    DiaryCommentUpdateReq updateReq,
                                                    Long currentUserId) {

        // 대상 댓글 + 게시글 매칭 검증
        DiaryComment comment = diaryCommentRepository
                .findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "해당 댓글이 존재하지 않거나 게시글과 매칭되지 않습니다."
                ));

        // 접근 권한(게시글 가시성) 확인
        DiaryPost post = comment.getPost();
        if (!canAccessPost(currentUserId, post)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "이 게시글에 접근할 수 없습니다.");
        }

        // 수정 권한 확인: 작성자만 수정 가능
        if (!comment.getAuthor().getId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "댓글 작성자만 수정할 수 있습니다.");
        }

        DiaryCommentRequestMapper.applyUpdate(comment, updateReq);

        return DiaryCommentUpdateRes.from(comment);
    }


    /**
     * 읽을 수 있는 글에만 댓글 작성 가능
     */
    private boolean canAccessPost(Long viewerId, DiaryPost post) {
        Long ownerId = post.getUser().getId();

        // 1. 본인 글은 항상 허용
        if (viewerId.equals(ownerId)) return true;

        // 2. 공개 범위에 따라 접근 허용 여부 결정
        return switch (post.getVisibility()) {
            case PUBLIC -> true;
            case FRIEND -> followService.isMutualFollow(viewerId, ownerId);
            case PRIVATE -> false;
        };
    }

}