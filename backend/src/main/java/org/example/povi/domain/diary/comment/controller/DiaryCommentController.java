package org.example.povi.domain.diary.comment.controller;


import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.povi.auth.token.jwt.CustomJwtUser;
import org.example.povi.domain.diary.comment.dto.request.DiaryCommentCreateReq;
import org.example.povi.domain.diary.comment.dto.response.DiaryCommentCreateRes;
import org.example.povi.domain.diary.comment.service.DiaryCommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diary-posts/{diaryPostId}/comments")
public class DiaryCommentController {

    private final DiaryCommentService diaryCommentService;

    @PostMapping
    @Operation(summary = "댓글 생성")
    public ResponseEntity<DiaryCommentCreateRes> createDiaryComment(
            @PathVariable Long diaryPostId,
            @RequestBody @Valid DiaryCommentCreateReq createReq,
            @AuthenticationPrincipal CustomJwtUser currentUser
    ){
        DiaryCommentCreateRes res = diaryCommentService.createDiaryComment(diaryPostId, createReq, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(res);

    }
}
