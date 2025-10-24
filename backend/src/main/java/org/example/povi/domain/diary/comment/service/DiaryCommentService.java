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
import org.example.povi.domain.diary.post.policy.DiaryPostAccessPolicy;
import org.example.povi.domain.diary.post.repository.DiaryPostRepository;
import org.example.povi.domain.user.entity.User;
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
    private final DiaryPostAccessPolicy postAccessPolicy;

    /**
     * 댓글 생성
     * - 로그인 필수
     * - 대상 포스트에 대한 읽기 권한 필요(공개/친구/본인)
     */
    @Transactional
    public DiaryCommentCreateRes createDiaryComment(Long postId,
                                                    DiaryCommentCreateReq req,
                                                    Long currentUserId) {

        requireLogin(currentUserId);

        User commenter = userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자가 존재하지 않습니다."));

        DiaryPost post = diaryPostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."));

        assertReadable(currentUserId, post, "댓글을 작성할 권한이 없습니다.");

        DiaryComment saved = diaryCommentRepository.save(
                DiaryCommentRequestMapper.toEntity(req, commenter, post)
        );
        return DiaryCommentCreateRes.from(saved);
    }


    /**
     * 댓글 목록 조회
     * - 비로그인 허용(공개글 조건은 Policy에서 처리)
     */
    @Transactional(readOnly = true)
    public PagedResponse<DiaryCommentRes> getCommentsByPost(Long postId,
                                                            Pageable pageable,
                                                            Long currentUserId) {

        requireLogin(currentUserId);

        DiaryPost post = diaryPostRepository.findById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."));

        assertReadable(currentUserId, post, "이 게시글에 접근할 수 없습니다.");

        Page<DiaryComment> page = diaryCommentRepository.findByPostId(postId, pageable);
        return DiaryCommentMapper.toPagedResponse(page, currentUserId);
    }

    /**
     * 댓글 수정
     * - 로그인 필수
     * - 대상 포스트 읽기 가능해야 함
     * - 댓글 작성자 본인만 수정 가능
     */
    @Transactional
    public DiaryCommentUpdateRes updateDiaryComment(Long postId,
                                                    Long commentId,
                                                    DiaryCommentUpdateReq req,
                                                    Long currentUserId) {
        requireLogin(currentUserId);

        DiaryComment comment = diaryCommentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "해당 댓글이 존재하지 않거나 게시글과 매칭되지 않습니다."
                ));

        DiaryPost post = comment.getPost();
        assertReadable(currentUserId, post, "이 게시글에 접근할 수 없습니다.");

        if (!comment.getAuthor().getId().equals(currentUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "댓글 작성자만 수정할 수 있습니다.");
        }

        DiaryCommentRequestMapper.updateEntity(comment, req);
        return DiaryCommentUpdateRes.from(comment);
    }

    /**
     * 댓글 삭제
     * - 로그인 필수
     * - 대상 포스트 읽기 가능해야 함
     * - 댓글 작성자 또는 게시글 작성자만 삭제 가능
     */
    @Transactional
    public void deleteDiaryComment(Long postId,
                                   Long commentId,
                                   Long currentUserId) {
        requireLogin(currentUserId);

        DiaryComment comment = diaryCommentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "해당 댓글이 존재하지 않거나 게시글과 매칭되지 않습니다."
                ));

        DiaryPost post = comment.getPost();
        assertReadable(currentUserId, post, "이 게시글에 접근할 수 없습니다.");

        Long authorId = comment.getAuthor().getId();
        Long postAuthorId = post.getUser().getId();
        if (!currentUserId.equals(authorId) && !currentUserId.equals(postAuthorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "댓글 작성자 또는 게시글 작성자만 삭제할 수 있습니다.");
        }

        diaryCommentRepository.delete(comment);
    }


    /** 로그인 필수 동작에서 userId null 방지 */
    private void requireLogin(Long userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "로그인이 필요합니다.");
        }
    }

    /** 포스트 읽기 권한 검사: 본인/공개/친구(상호팔로우) */
    private void assertReadable(Long userId, DiaryPost post, String forbiddenMsg) {
        if (!postAccessPolicy.hasReadPermission(userId, post)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, forbiddenMsg);
        }
    }

    /** 댓글 존재 + 특정 포스트에 속하는지 매칭 검증 */
    private DiaryComment getCommentOr404(Long commentId, Long postId) {
        return diaryCommentRepository.findByIdAndPostId(commentId, postId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "해당 댓글이 존재하지 않거나 게시글과 매칭되지 않습니다."
                ));
    }
}
