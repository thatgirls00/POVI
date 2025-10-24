package org.example.povi.domain.community.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.example.povi.domain.community.dto.request.CommentCreateRequest;
import org.example.povi.domain.community.dto.request.PostCreateRequest;
import org.example.povi.domain.community.dto.request.PostUpdateRequest;
import org.example.povi.domain.community.dto.response.BookmarkListResponse;
import org.example.povi.domain.community.dto.response.CommentCreateResponse;
import org.example.povi.domain.community.dto.response.CommentDeleteResponse;
import org.example.povi.domain.community.dto.response.CommentListResponse;
import org.example.povi.domain.community.dto.response.LikeListResponse;
import org.example.povi.domain.community.dto.response.LikeResponse;
import org.example.povi.domain.community.dto.response.PostBookmarkResponse;
import org.example.povi.domain.community.dto.response.PostCreateResponse;
import org.example.povi.domain.community.dto.response.PostDeleteResponse;
import org.example.povi.domain.community.dto.response.PostDetailResponse;
import org.example.povi.domain.community.dto.response.PostListResponse;
import org.example.povi.domain.community.dto.response.PostUpdateResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "Community", description = "커뮤니티 API")
@RequestMapping("/posts")
public interface CommunityControllerDocs {

    @Operation(summary = "커뮤니티 글 작성", description = "커뮤니티에 익명 글을 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "작성 성공", content =
            @Content(schema = @Schema(implementation = PostCreateResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 실패")
    })
    @PostMapping
    ResponseEntity<PostCreateResponse> createPost(
            @RequestHeader("Authorization") String bearerToken,
            @RequestBody @Valid PostCreateRequest request);


    @Operation(summary = "커뮤니티 글 삭제", description = "커뮤니티에 작성한 익명 글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공", content =
            @Content(schema = @Schema(implementation = PostDeleteResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @DeleteMapping("/{postId}")
    ResponseEntity<PostDeleteResponse> deletePost(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long postId);


    @Operation(summary = "커뮤니티 글 수정", description = "커뮤니티에 작성한 익명 글을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "수정 성공", content =
            @Content(schema = @Schema(implementation = PostUpdateResponse.class))),
            @ApiResponse(responseCode = "400", description = "요청 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @PutMapping(value = "/{postId}", consumes = "multipart/form-data")
    ResponseEntity<PostUpdateResponse> updatePost(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long postId,
            @Valid @RequestPart("request") PostUpdateRequest request,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos);


    @Operation(summary = "커뮤니티 글 목록 조회", description = "커뮤니티 글 목록을 페이지네이션으로 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content =
            @Content(array = @ArraySchema(schema = @Schema(implementation = PostListResponse.class)))),
            @ApiResponse(responseCode = "400", description = "요청 실패")
    })
    @GetMapping
    ResponseEntity<Page<PostListResponse>> getPostList(
            @PageableDefault(size = 10, sort = "createdAt,desc") Pageable pageable);


    @Operation(summary = "내가 작성한 글 목록 조회", description = "내가 작성한 커뮤니티 글 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content =
            @Content(array = @ArraySchema(schema = @Schema(implementation = PostListResponse.class)))),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/me")
    ResponseEntity<Page<PostListResponse>> getMyPostList(
            @RequestHeader("Authorization") String bearerToken,
            @PageableDefault(size = 4, sort = "createdAt,desc") Pageable pageable);


    @Operation(summary = "커뮤니티 글 상세 조회", description = "특정 커뮤니티 글의 상세 내용을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content =
            @Content(schema = @Schema(implementation = PostDetailResponse.class))),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @GetMapping("/{postId}")
    ResponseEntity<PostDetailResponse> getPostDetail(@PathVariable Long postId);


    @Operation(summary = "댓글 작성", description = "특정 게시글에 댓글을 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "작성 성공", content =
            @Content(schema = @Schema(implementation = CommentCreateResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "게시글 없음")
    })
    @PostMapping("/{postId}/comments")
    ResponseEntity<CommentCreateResponse> createComment(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long postId,
            @Valid @RequestBody CommentCreateRequest request);


    @Operation(summary = "댓글 삭제", description = "특정 댓글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "403", description = "권한 없음"),
            @ApiResponse(responseCode = "404", description = "댓글 없음")
    })
    @DeleteMapping("/comments/{commentId}")
    ResponseEntity<CommentDeleteResponse> deleteComment(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long commentId);


    @Operation(summary = "내가 작성한 댓글 목록", description = "내가 작성한 댓글 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content =
            @Content(array = @ArraySchema(schema = @Schema(implementation = CommentListResponse.class)))),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/me/comments")
    ResponseEntity<Page<CommentListResponse>> getMyComments(
            @RequestHeader("Authorization") String bearerToken,
            @PageableDefault(size = 2, sort = "createdAt,desc") Pageable pageable);


    @Operation(summary = "댓글 좋아요 추가", description = "특정 댓글에 좋아요를 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content =
            @Content(schema = @Schema(implementation = LikeResponse.class))),
            @ApiResponse(responseCode = "404", description = "댓글 없음")
    })
    @PostMapping("/comments/{commentId}/like")
    ResponseEntity<LikeResponse> addLikeToComment(@PathVariable Long commentId);


    @Operation(summary = "댓글 좋아요 삭제", description = "특정 댓글의 좋아요를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content =
            @Content(schema = @Schema(implementation = LikeResponse.class))),
            @ApiResponse(responseCode = "404", description = "댓글 없음")
    })
    @DeleteMapping("/comments/{commentId}/like")
    ResponseEntity<LikeResponse> removeLikeFromComment(@PathVariable Long commentId);


    @Operation(summary = "게시글 좋아요 추가", description = "특정 게시글에 좋아요를 추가합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content =
            @Content(schema = @Schema(implementation = LikeResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "게시글 없음"),
            @ApiResponse(responseCode = "409", description = "이미 좋아요 누름")
    })
    @PostMapping("/{postId}/like")
    ResponseEntity<LikeResponse> addLikeToPost(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long postId);


    @Operation(summary = "게시글 좋아요 삭제", description = "특정 게시글의 좋아요를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공", content =
            @Content(schema = @Schema(implementation = LikeResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "좋아요/게시글 없음")
    })
    @DeleteMapping("/{postId}/like")
    ResponseEntity<LikeResponse> removeLikeFromPost(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long postId);


    @Operation(summary = "내가 좋아요 누른 게시글 목록", description = "내가 좋아요를 누른 게시글 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content =
            @Content(array = @ArraySchema(schema = @Schema(implementation = LikeListResponse.class)))),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/me/likes")
    ResponseEntity<Page<LikeListResponse>> getMyLikedPosts(
            @RequestHeader("Authorization") String bearerToken,
            @PageableDefault(size = 2, sort = "createdAt,desc") Pageable pageable);


    @Operation(summary = "게시글 북마크 추가", description = "특정 게시글을 북마크합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "북마크 성공", content =
            @Content(schema = @Schema(implementation = PostBookmarkResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "게시글 없음"),
            @ApiResponse(responseCode = "409", description = "이미 북마크됨")
    })
    @PostMapping("/{postId}/bookmark")
    ResponseEntity<PostBookmarkResponse> addBookmark(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long postId);


    @Operation(summary = "게시글 북마크 삭제", description = "특정 게시글의 북마크를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "삭제 성공", content =
            @Content(schema = @Schema(implementation = PostBookmarkResponse.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "404", description = "북마크/게시글 없음")
    })
    @DeleteMapping("/{postId}/bookmark")
    ResponseEntity<PostBookmarkResponse> removeBookmark(
            @RequestHeader("Authorization") String bearerToken,
            @PathVariable Long postId);


    @Operation(summary = "내가 북마크한 글 목록", description = "내가 북마크한 게시글 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공", content =
            @Content(array = @ArraySchema(schema = @Schema(implementation = BookmarkListResponse.class)))),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @GetMapping("/me/bookmarks")
    ResponseEntity<Page<BookmarkListResponse>> getMyBookmarks(
            @RequestHeader("Authorization") String bearerToken,
            @PageableDefault(size = 4, sort = "createdAt,desc") Pageable pageable);
}
