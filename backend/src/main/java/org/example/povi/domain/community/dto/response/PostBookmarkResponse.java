package org.example.povi.domain.community.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "게시글 북마크 응답 DTO")
public record PostBookmarkResponse(
        Long postId,
        String message
) {

}
