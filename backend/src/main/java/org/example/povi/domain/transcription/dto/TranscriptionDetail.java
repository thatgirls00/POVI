package org.example.povi.domain.transcription.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.povi.domain.transcription.entity.Transcription;

import java.time.LocalDateTime;

@Schema(description = "필사 상세 응답 DTO")
public record TranscriptionDetail(

        @Schema(description = "필사 ID", example = "1")
        Long transcriptionId,

        @Schema(description = "필사한 문장 내용", example = "내가 제일 좋아하는 문장이에요.")
        String content,

        @Schema(description = "명언 원문 내용", example = "삶은 자전거를 타는 것과 같다. 균형을 잡으려면 움직여야 한다.")
        String quoteContent,

        @Schema(description = "명언 저자", example = "알베르트 아인슈타인")
        String quoteAuthor,

        @Schema(description = "작성 일시", example = "2025-10-24T14:30:00")
        LocalDateTime createdAt

) {
    public static TranscriptionDetail fromEntity(Transcription transcription) {
        return new TranscriptionDetail(
                transcription.getId(),
                transcription.getContent(),
                transcription.getQuote().getContent(),
                transcription.getQuote().getAuthor(),
                transcription.getCreatedAt()
        );
    }
}