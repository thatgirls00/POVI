package org.example.povi.domain.transcription.dto;

import jakarta.validation.constraints.NotBlank;

public record TranscriptionReq(
        @NotBlank(message = "필사 내용은 비어있을 수 없습니다.")
        String content
) {}
