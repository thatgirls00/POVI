package org.example.povi.domain.diary.comment.service;

import lombok.RequiredArgsConstructor;
import org.example.povi.domain.diary.comment.dto.request.DiaryCommentCreateReq;
import org.example.povi.domain.diary.comment.dto.response.DiaryCommentCreateRes;
import org.example.povi.domain.diary.comment.entity.DiaryComment;
import org.example.povi.domain.diary.comment.mapper.DiaryCommentRequestMapper;
import org.example.povi.domain.diary.comment.repository.DiaryCommentRepository;
import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.example.povi.domain.diary.post.repository.DiaryPostRepository;
import org.example.povi.domain.user.entity.User;
import org.example.povi.domain.user.follow.service.FollowService;
import org.example.povi.domain.user.repository.UserRepository;
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
    public DiaryCommentCreateRes createDiaryComment(Long diaryPostId,
                                                    DiaryCommentCreateReq createReq,
                                                    Long commenterId) {

        // 댓글 작성자 및 게시글 검증
        User commenter = userRepository.findById(commenterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자가 존재하지 않습니다."));

        DiaryPost diaryPost = diaryPostRepository.findById(diaryPostId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "게시글이 존재하지 않습니다."));

        // 접근 권한 확인
        if (!hasCommentPermission(commenter, diaryPost)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "댓글을 작성할 권한이 없습니다.");
        }

        DiaryComment toSave = DiaryCommentRequestMapper.fromCreateRequest(createReq, commenter, diaryPost);
        DiaryComment saved = diaryCommentRepository.save(toSave);
        return DiaryCommentCreateRes.from(saved);
    }



    /**
     * 읽을 수 있는 글에만 댓글 작성 가능
     */
    private boolean hasCommentPermission(User commenter, DiaryPost targetPost) {
        Long commenterId = commenter.getId();
        Long authorId = targetPost.getUser().getId();

        if (commenterId.equals(authorId)) return true; // 본인 글은 허용

        return switch (targetPost.getVisibility()) {
            case PUBLIC -> true;
            case FRIEND -> followService.isMutualFollow(authorId, authorId);
            case PRIVATE -> false;
        };
    }
}