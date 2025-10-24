package org.example.povi.domain.diary.comment.controller;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.povi.auth.token.jwt.CustomJwtUser;
import org.example.povi.domain.diary.comment.dto.request.DiaryCommentCreateReq;
import org.example.povi.domain.diary.comment.dto.request.DiaryCommentUpdateReq;
import org.example.povi.domain.diary.comment.dto.response.DiaryCommentCreateRes;
import org.example.povi.domain.diary.comment.dto.response.DiaryCommentRes;
import org.example.povi.domain.diary.comment.dto.response.DiaryCommentUpdateRes;
import org.example.povi.domain.diary.comment.service.DiaryCommentService;
import org.example.povi.global.dto.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diary-posts/{postId}/comments")
public class DiaryCommentController implements DiaryCommentControllerDocs {

    private final DiaryCommentService diaryCommentService;

    @PostMapping
    @Operation(summary = "댓글 생성")
    public ResponseEntity<DiaryCommentCreateRes> createDiaryComment(
            @PathVariable Long postId,
            @RequestBody @Valid DiaryCommentCreateReq createReq,
            @AuthenticationPrincipal CustomJwtUser currentUser
    ){
        DiaryCommentCreateRes res = diaryCommentService.createDiaryComment(postId, createReq, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @GetMapping
    @Operation(summary = "댓글 조회")
    public ResponseEntity<PagedResponse<DiaryCommentRes>> getComments(
            @PathVariable Long postId,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
            @AuthenticationPrincipal CustomJwtUser currentUser
    ) {
        PagedResponse<DiaryCommentRes>  res = diaryCommentService.getCommentsByPost(
                postId, pageable, currentUser.getId()
        );
        return ResponseEntity.ok(res);
    }

    @PatchMapping("/{commentId}")
    @Operation(summary = "댓글 수정")
    public ResponseEntity<DiaryCommentUpdateRes> updateDiaryComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody @Valid DiaryCommentUpdateReq updateReq,
            @AuthenticationPrincipal CustomJwtUser currentUser
    ) {
        DiaryCommentUpdateRes res = diaryCommentService.updateDiaryComment(
                postId, commentId, updateReq, currentUser.getId()
        );
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제")
    public ResponseEntity<Void> deleteDiaryComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomJwtUser currentUser
    ){
        diaryCommentService.deleteDiaryComment(postId, commentId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
}
