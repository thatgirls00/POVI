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

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diaries")
public class DiaryPostController {

    private final DiaryPostService diaryPostService;

    @PostMapping
    @Operation(summary = "다이어리 생성")
    public ResponseEntity<DiaryPostCreateRes> createDiaryPost(
            @RequestBody @Valid DiaryPostCreateReq createReq,
            @AuthenticationPrincipal CustomJwtUser currentUser
    ) {
        DiaryPostCreateRes res = diaryPostService.createDiaryPost(createReq, currentUser.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(res);
    }

    @PatchMapping("/{diaryPostId}")
    @Operation(summary = "다이어리 부분수정")
    public ResponseEntity<DiaryPostUpdateRes> updateDiaryPost(
            @PathVariable Long diaryPostId,
            @RequestBody @Valid DiaryPostUpdateReq updateReq,
            @AuthenticationPrincipal CustomJwtUser currentUser

    ) {
        DiaryPostUpdateRes res = diaryPostService.updateDiaryPost(diaryPostId, updateReq, currentUser.getId());
        return ResponseEntity.ok(res);
    }


    @DeleteMapping("/{diaryPostId}")
    @Operation(summary = "다이어리 삭제")
    public ResponseEntity<Void> deleteDiaryPost(
            @PathVariable Long diaryPostId,
            @AuthenticationPrincipal CustomJwtUser currentUser
    ) {
        diaryPostService.deleteDiaryPost(diaryPostId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{diaryPostId}")
    @Operation(summary = "단일 상세 조회")
    public ResponseEntity<DiaryDetailRes> getMyDiaryPostDetail(
            @PathVariable Long diaryPostId,
            @AuthenticationPrincipal CustomJwtUser currentUser
    ) {
        DiaryDetailRes res = diaryPostService.getMyDiaryPostDetail(diaryPostId, currentUser.getId());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/mine")
    @Operation(summary = "내 다이어리 목록 + 주간 통계")
    public ResponseEntity<MyDiaryListRes> listMyDiaries(
            @AuthenticationPrincipal CustomJwtUser currentUser
    ) {
        MyDiaryListRes res = diaryPostService.getMyDiaryPostsWithWeeklyStats(currentUser.getId());
        return ResponseEntity.ok(res);
    }

    @GetMapping("/friends")
    @Operation(summary = "친구 다이어리 - 맞팔이면 FRIEND+PUBLIC, 단방향이면 PUBLIC만")
    public ResponseEntity<List<DiaryPostCardRes>> listFriendDiaries(
            @AuthenticationPrincipal CustomJwtUser currentUser
    ) {
        List<DiaryPostCardRes> res = diaryPostService.listFriendDiaries(currentUser.getId());
        return ResponseEntity.ok(res);

    }

    @GetMapping("/explore")
    @Operation(summary = "모두의 다이어 - 맞팔: FRIEND+PUBLIC, 그 외: PUBLIC)")
    public ResponseEntity<List<DiaryPostCardRes>> explore(
            @AuthenticationPrincipal CustomJwtUser currentUser
    ) {
        List<DiaryPostCardRes> res = diaryPostService.listExploreFeed(currentUser.getId());
        return ResponseEntity.ok(res);
    }
}



