package org.example.povi.domain.community.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "좋아요 처리 결과 응답 DTO")
public record LikeResponse(
        @Schema(description = "대상의 ID (게시글 또는 댓글)")
        Long targetId,
        @Schema(description = "업데이트된 총 좋아요 수")
        int likeCount
) {

}
