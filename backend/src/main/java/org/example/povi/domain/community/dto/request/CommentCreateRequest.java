package org.example.povi.domain.community.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "댓글 작성 요청 DTO")
public record CommentCreateRequest(
        @NotBlank(message = "댓글 내용은 비워둘 수 없습니다.")
        @Size(max = 1000, message = "댓글은 1000자를 초과할 수 없습니다.")
        @Schema(description = "댓글 본문", example = "정말 유용한 정보네요!")
        String content
) {}
