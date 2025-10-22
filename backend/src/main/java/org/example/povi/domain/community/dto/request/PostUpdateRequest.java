package org.example.povi.domain.community.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import org.example.povi.domain.community.entity.CommunityEmoticon;

public record PostUpdateRequest(
        @NotBlank(message = "제목은 필수 입력 항목입니다.")
        @Schema(description = "게시글 제목", example = "수정된 제목")
        String title,
        @NotBlank(message = "내용은 비워둘 수 없습니다.")
        @Schema(description = "수정할 게시글 본문", example = "새롭게 수정한 내용입니다.")
        String content,
        @Schema(description = "수정할 사진 URL 목록", example = "[\"url1.jpg\", \"url2.jpg\"]")
        List<String> photoUrls,
        CommunityEmoticon emoticon

) {
}
