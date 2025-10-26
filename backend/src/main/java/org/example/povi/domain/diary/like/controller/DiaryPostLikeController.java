package org.example.povi.domain.diary.like.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.example.povi.auth.token.jwt.CustomJwtUser;
import org.example.povi.domain.diary.like.dto.DiaryPostLikeRes;
import org.example.povi.domain.diary.like.service.DiaryPostLikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diary-posts/{postId}/likes")
public class DiaryPostLikeController {

    private final DiaryPostLikeService diaryPostLikeService;

    @PostMapping("/toggle")
    @Operation(summary = "좋아요 토글", description = "이미 눌렀다면 취소, 아니면 추가합니다.")
    public ResponseEntity<DiaryPostLikeRes> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomJwtUser currentUser
    ) {
        Long userId = currentUser.getId();
        DiaryPostLikeRes response = diaryPostLikeService.toggle(postId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(summary = "좋아요 여부 조회")
    public ResponseEntity<DiaryPostLikeRes> isLiked(
            @PathVariable Long postId,
            @AuthenticationPrincipal CustomJwtUser currentUser
    ) {
        Long userId = currentUser.getId();
        DiaryPostLikeRes response = diaryPostLikeService.me(postId, userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    @Operation(summary = "좋아요 수 조회")
    public ResponseEntity<Long> countLikes(@PathVariable Long postId) {
        long count = diaryPostLikeService.count(postId);
        return ResponseEntity.ok(count);
    }
}