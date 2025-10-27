package org.example.povi.domain.community.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.povi.auth.token.jwt.JwtTokenProvider;
import org.example.povi.domain.community.dto.request.CommentCreateRequest;
import org.example.povi.domain.community.dto.request.PostCreateRequest;
import org.example.povi.domain.community.dto.response.*;
import org.example.povi.domain.community.dto.request.PostUpdateRequest;
import org.example.povi.domain.community.service.CommunityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/posts")
@RequiredArgsConstructor
public class CommunityController implements CommunityControllerDocs{

    private final CommunityService communityService;
    private final JwtTokenProvider jwtUtil;


    @PostMapping
    public ResponseEntity<PostCreateResponse> createPost(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody @Valid PostCreateRequest request) {

        String rawToken = bearerToken.replace("Bearer ", "");
        Long uerId = jwtUtil.getUserId(rawToken);

        PostCreateResponse response = communityService.createPost(uerId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @DeleteMapping("/{postId}")
    public ResponseEntity<PostDeleteResponse> deletePost(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long postId) {

        String rawToken = bearerToken.replace("Bearer ", "");
        Long userId = jwtUtil.getUserId(rawToken);

        PostDeleteResponse response = communityService.deletePost(userId, postId);
        return ResponseEntity.ok(response);
    }


    @PutMapping("/{postId}")
    public ResponseEntity<PostUpdateResponse> updatePost(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long postId,
            @Valid @RequestPart("request") PostUpdateRequest request,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos) {

        String rawToken = bearerToken.replace("Bearer ", "");
        Long userId = jwtUtil.getUserId(rawToken);

        PostUpdateResponse response = communityService.updatePost(userId, postId, request, photos);
        return ResponseEntity.ok(response);
    }


    @GetMapping
    public ResponseEntity<Page<PostListResponse>> getPostList(
            @PageableDefault(size = 10, sort = "createdAt,desc") Pageable pageable) {
        Page<PostListResponse> postList = communityService.getPostList(pageable);
        return ResponseEntity.ok(postList);
    }


    @GetMapping("/me")
    public ResponseEntity<Page<PostListResponse>> getMyPostList(
            @RequestHeader("Authorization") String bearerToken,
            @PageableDefault(size = 4, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable
    ) {
        Long userId = jwtUtil.getUserId(bearerToken.replace("Bearer ", ""));
        Page<PostListResponse> postList = communityService.getMyPostList(userId, pageable);
        return ResponseEntity.ok(postList);
    }


    @Operation(summary = "커뮤니티 글 상세보기", description = "커뮤니티에 익명 글을 클릭했을때 .")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(
                                    schema = @Schema(implementation = PostDetailResponse.class)
                            ))
            }),
            @ApiResponse(responseCode = "400", description = "실패")
    })
    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPostDetail(@PathVariable Long postId) {
        PostDetailResponse postDetail = communityService.getPostDetail(postId);
        return ResponseEntity.ok(postDetail);
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentCreateResponse> createComment(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest request) {
        Long userId = jwtUtil.getUserId(bearerToken.replace("Bearer ", ""));
        CommentCreateResponse response = communityService.createComment(userId, postId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<CommentDeleteResponse> deleteComment(String bearerToken, Long commentId) {
        Long userId = jwtUtil.getUserId(bearerToken.replace("Bearer ", ""));
        communityService.deleteComment(userId, commentId);
        return ResponseEntity.ok().build();
    }

    // 내 댓글내역
    @GetMapping("/me/comments")
    public ResponseEntity<Page<CommentListResponse>> getMyComments(
            @RequestHeader("Authorization") String bearerToken,
            @PageableDefault(size = 2, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable
    ) {
        Long userId = jwtUtil.getUserId(bearerToken.replace("Bearer ", ""));
        Page<CommentListResponse> response = communityService.getMyComments(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/comments/{commentId}/like")
    public ResponseEntity<LikeResponse> addLikeToComment(@PathVariable Long commentId) {
        LikeResponse response = communityService.addLikeToComment(commentId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comments/{commentId}/like")
    public ResponseEntity<LikeResponse> removeLikeFromComment(@PathVariable Long commentId) {
        LikeResponse response = communityService.removeLikeFromComment(commentId);
        return ResponseEntity.ok(response);

    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<LikeResponse> addLikeToPost(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long postId) {
        Long userId = jwtUtil.getUserId(bearerToken.replace("Bearer ", ""));
        LikeResponse response = communityService.addLikeToPost(userId, postId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<LikeResponse> removeLikeFromPost(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long postId) {
        Long userId = jwtUtil.getUserId(bearerToken.replace("Bearer ", ""));
        LikeResponse response = communityService.removeLikeFromPost(userId, postId);
        return ResponseEntity.ok(response);
    }

    // 내가 좋아요 누른 게시글
    @GetMapping("/me/likes")
    public ResponseEntity<Page<LikeListResponse>> getMyLikedPosts(
            @RequestHeader("Authorization") String bearerToken,
            @PageableDefault(size = 2, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable) {

        Long userId = jwtUtil.getUserId(bearerToken.replace("Bearer ", ""));
        Page<LikeListResponse> response = communityService.getMyLikedPosts(userId, pageable);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{postId}/bookmark")
    public ResponseEntity<PostBookmarkResponse> addBookmark(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long postId) {

        Long userId = jwtUtil.getUserId(bearerToken.replace("Bearer ", ""));
        PostBookmarkResponse response = communityService.addBookmark(userId, postId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{postId}/bookmark")
    public ResponseEntity<PostBookmarkResponse> removeBookmark(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long postId) {

        Long userId = jwtUtil.getUserId(bearerToken.replace("Bearer ", ""));
        PostBookmarkResponse response = communityService.removeBookmark(userId, postId);
        return ResponseEntity.ok(response);
    }

    // 내가 북마크한 글 목록
    @GetMapping("/me/bookmarks")
    public ResponseEntity<Page<BookmarkListResponse>> getMyBookmarks(
            @RequestHeader("Authorization") String bearerToken,
            @PageableDefault(size = 4, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable
    ) {
        Long userId = jwtUtil.getUserId(bearerToken.replace("Bearer ", ""));
        Page<BookmarkListResponse> response = communityService.getMyBookmarkedPosts(userId, pageable);
        return ResponseEntity.ok(response);
    }
}
