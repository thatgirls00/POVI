package org.example.povi.domain.community.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "게시글 수정 응답 DTO")
public record PostUpdateResponse(
        @Schema(description = "수정된 게시글 ID", example = "1")
        Long postId,

        @Schema(description = "수정된 제목", example = "수정된 제목입니다")
        String title,

        @Schema(description = "수정된 내용", example = "새롭게 수정한 내용입니다.")
        String content,

        @Schema(description = "새롭게 등록된 사진 URL 목록", example = "[\"url1.jpg\", \"url2.jpg\"]")
        List<String> photoUrls,

        @Schema(description = "응답 메시지", example = "게시글이 성공적으로 수정되었습니다.")
        String message
) {
}
