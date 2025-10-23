package org.example.povi.domain.diary.comment.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DiaryCommentUpdateReq(
        @NotBlank
        @Size(min = 1, max = 2000)
        String content
) {}