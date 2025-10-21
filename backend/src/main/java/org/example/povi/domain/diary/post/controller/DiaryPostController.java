package org.example.povi.domain.diary.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.povi.auth.token.jwt.CustomJwtUser;
import org.example.povi.domain.diary.post.dto.request.DiaryPostCreateReq;
import org.example.povi.domain.diary.post.dto.request.DiaryPostUpdateReq;
import org.example.povi.domain.diary.post.dto.response.*;
import org.example.povi.domain.diary.post.service.DiaryPostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diaries")
public class DiaryPostController {

    private final DiaryPostService diaryPostService;

    @PostMapping
    @Operation(summary = "다이어리 생성")
    public ResponseEntity<DiaryPostCreateRes> createPost(
            @RequestBody @Valid DiaryPostCreateReq request,
            @AuthenticationPrincipal CustomJwtUser user
    ) {
        DiaryPostCreateRes response = diaryPostService.createPost(request, user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{postId}")
    @Operation(summary = "다이어리 부분수정")
    public ResponseEntity<DiaryPostUpdateRes> patchPost(
            @PathVariable("postId") Long postId,
            @RequestBody @Valid DiaryPostUpdateReq request,
            @AuthenticationPrincipal CustomJwtUser user

    ) {
        DiaryPostUpdateRes response = diaryPostService.patchPost(postId, request, user.getId());
        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/{postId}")
    @Operation(summary = "다이어리 삭제")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomJwtUser user
    ) {
        diaryPostService.deletePost(postId, user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}")
    @Operation(summary = "단일 상세 조회")
    public ResponseEntity<DiaryDetailRes> getPostDetail(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomJwtUser user
    ) {
        DiaryDetailRes response = diaryPostService.getPostDetail(postId, user.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mine")
    @Operation(summary = "나의 다이어리 목록 조회")
    public ResponseEntity<MyDiaryListRes> getMyPosts(
            @AuthenticationPrincipal CustomJwtUser user
    ) {
        MyDiaryListRes response = diaryPostService.getMyPosts(user.getId());
        return ResponseEntity.ok(response);
    }

}

