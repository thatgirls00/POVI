package org.example.povi.domain.diary.comment.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.povi.auth.token.jwt.CustomJwtUser;
import org.example.povi.domain.diary.comment.dto.request.DiaryCommentCreateReq;
import org.example.povi.domain.diary.comment.dto.request.DiaryCommentUpdateReq;
import org.example.povi.domain.diary.comment.dto.response.DiaryCommentCreateRes;
import org.example.povi.domain.diary.comment.dto.response.DiaryCommentRes;
import org.example.povi.domain.diary.comment.dto.response.DiaryCommentUpdateRes;
import org.example.povi.global.dto.PagedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Tag(name = "Diary Comments", description = "일기 댓글 API")
@RequestMapping("/diary-posts/{postId}/comments")
public interface DiaryCommentControllerDocs {


    @PostMapping
    @Operation(summary = "일기 댓글 생성")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "댓글 생성 성공", content =
            @Content(schema = @Schema(implementation = DiaryCommentCreateRes.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "일기 게시글을 찾을 수 없음")
    })
    ResponseEntity<DiaryCommentCreateRes> createDiaryComment(
            @PathVariable Long postId,
            @RequestBody @Valid DiaryCommentCreateReq createReq,
            @AuthenticationPrincipal CustomJwtUser currentUser
    );

    @GetMapping
    @Operation(summary = "일기 댓글 조회")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 조회 성공", content =
            @Content(schema = @Schema(implementation = PagedResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "일기 게시글을 찾을 수 없음")
    })
    ResponseEntity<PagedResponse<DiaryCommentRes>> getComments(
            @PathVariable Long postId,
            @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
            @AuthenticationPrincipal CustomJwtUser currentUser
    );

    @PatchMapping("/{commentId}")
    @Operation(summary = "일기 댓글 수정")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공", content =
            @Content(schema = @Schema(implementation = DiaryCommentUpdateRes.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "일기/댓글을 찾을 수 없음")
    })
    ResponseEntity<DiaryCommentUpdateRes> updateDiaryComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody @Valid DiaryCommentUpdateReq updateReq,
            @AuthenticationPrincipal CustomJwtUser currentUser
    );

    @DeleteMapping("/{commentId}")
    @Operation(summary = "일기 댓글 삭제")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "댓글 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "일기/댓글을 찾을 수 없음")
    })
    ResponseEntity<Void> deleteDiaryComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal CustomJwtUser currentUser
    );
}
