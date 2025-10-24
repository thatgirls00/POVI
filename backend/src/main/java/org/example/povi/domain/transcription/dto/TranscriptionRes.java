package org.example.povi.domain.transcription.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.povi.domain.transcription.entity.Transcription;

import java.time.LocalDateTime;

@Schema(description = "필사 생성 응답 DTO")
public record TranscriptionRes(

        @Schema(description = "필사 ID", example = "1")
        Long transcriptionId,

        @Schema(description = "필사 내용", example = "오늘 하루도 잘 해냈어. 수고했어.")
        String content,

        @Schema(description = "작성 시각", example = "2025-10-24T21:32:00")
        LocalDateTime createdAt

) {
    public static TranscriptionRes fromEntity(Transcription transcription) {
        return new TranscriptionRes(
                transcription.getId(),
                transcription.getContent(),
                transcription.getCreatedAt()
        );
    }
}