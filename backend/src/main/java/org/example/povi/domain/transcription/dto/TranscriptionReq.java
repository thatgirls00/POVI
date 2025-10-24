package org.example.povi.domain.transcription.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "필사 생성 요청 DTO")
public record TranscriptionReq(

        @NotBlank(message = "필사 내용은 비어있을 수 없습니다.")
        @Schema(description = "사용자가 작성한 필사 내용", example = "삶이 있는 한 희망은 있다.")
        String content

) {}