package org.example.povi.domain.diary.post.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.povi.domain.diary.post.dto.request.DiaryPostCreateReq;
import org.example.povi.domain.diary.post.dto.request.DiaryPostUpdateReq;
import org.example.povi.domain.diary.post.dto.response.*;
import org.example.povi.domain.diary.post.service.DiaryPostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diary-posts")
public class DiaryPostController {

    private final DiaryPostService diaryPostService;

    @PostMapping
    @Operation(summary = "다이어리 생성")
    public ResponseEntity<DiaryPostCreateRes> createDiaryPost(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @RequestBody @Valid DiaryPostCreateReq createReq
    ) {
        DiaryPostCreateRes res = diaryPostService.createDiaryPost(createReq, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PatchMapping("/{postId}")
    @Operation(summary = "다이어리 수정")
    public ResponseEntity<DiaryPostUpdateRes> updateDiaryPost(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable Long postId,
            @RequestBody @Valid DiaryPostUpdateReq updateReq
    ) {
        DiaryPostUpdateRes res = diaryPostService.updateDiaryPost(postId, updateReq, userId);
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/{postId}")
    @Operation(summary = "다이어리 삭제")
    public ResponseEntity<Void> deleteDiaryPost(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable Long postId
    ) {
        diaryPostService.deleteDiaryPost(postId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{postId}")
    @Operation(summary = "다이어리 상세 조회 (권한 규칙 적용)")
    public ResponseEntity<DiaryDetailRes> getMyDiaryPostDetail(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable Long postId
    ) {
        DiaryDetailRes res = diaryPostService.getDiaryPostDetail(postId, userId);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/mine")
    @Operation(summary = "내 다이어리 목록 + 주간 통계")
    public ResponseEntity<MyDiaryListRes> listMyDiaries(
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        MyDiaryListRes res = diaryPostService.getMyDiaryPostsWithWeeklyStats(userId);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/friends")
    @Operation(summary = "친구 다이어리 - 맞팔이면 FRIEND+PUBLIC, 단방향이면 PUBLIC만")
    public ResponseEntity<List<DiaryPostCardRes>> listFriendDiaries(
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        List<DiaryPostCardRes> res = diaryPostService.listFriendDiaries(userId);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/explore")
    @Operation(summary = "모두의 다이어리 - 맞팔: FRIEND+PUBLIC, 그 외: PUBLIC")
    public ResponseEntity<List<DiaryPostCardRes>> explore(
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        List<DiaryPostCardRes> res = diaryPostService.listExploreFeed(userId);
        return ResponseEntity.ok(res);
    }
}


