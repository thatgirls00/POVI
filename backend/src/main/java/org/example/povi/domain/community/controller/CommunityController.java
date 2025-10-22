package org.example.povi.domain.community.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.povi.auth.token.jwt.JwtTokenProvider;
import org.example.povi.domain.community.dto.request.PostCreateRequest;
import org.example.povi.domain.community.dto.response.LikeResponse;
import org.example.povi.domain.community.dto.response.PostBookmarkResponse;
import org.example.povi.domain.community.dto.response.PostCreateResponse;
import org.example.povi.domain.community.dto.response.PostDeleteResponse;
import org.example.povi.domain.community.dto.request.PostUpdateRequest;
import org.example.povi.domain.community.dto.response.PostDetailResponse;
import org.example.povi.domain.community.dto.response.PostListResponse;
import org.example.povi.domain.community.dto.response.PostUpdateResponse;
import org.example.povi.domain.community.service.CommunityService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
public class CommunityController {

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

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponse> getPostDetail(@PathVariable Long postId) {
        PostDetailResponse postDetail = communityService.getPostDetail(postId);
        return ResponseEntity.ok(postDetail);
    }


    @PostMapping("/{postId}/like")
    public ResponseEntity<LikeResponse> addLikeToPost(@PathVariable Long postId) {
        LikeResponse response = communityService.addLikeToPost(postId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<LikeResponse> removeLikeFromPost(@PathVariable Long postId) {
        LikeResponse response = communityService.removeLikeFromPost(postId);
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

}
