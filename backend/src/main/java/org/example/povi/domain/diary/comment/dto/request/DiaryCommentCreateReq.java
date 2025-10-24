package org.example.povi.domain.diary.comment.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "다이어리 댓글 작성 요청 DTO")
public record DiaryCommentCreateReq(
        @NotBlank @Size(min = 1, max = 2000)
        String content
) {
}
