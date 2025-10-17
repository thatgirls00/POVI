package org.example.povi.transcription.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.povi.domain.Transcription;

import java.time.LocalDateTime;

@Getter
@Builder
public class TranscriptionDetail {
    private final Long transcriptionId;
    private final String content;
    private final String quoteContent; // 어떤 명언을 필사했는지
    private final String quoteAuthor;
    private final LocalDateTime createdAt;

    public static TranscriptionDetail fromEntity(Transcription transcription) {
        return TranscriptionDetail.builder()
                .transcriptionId(transcription.getId())
                .content(transcription.getContent())
                .quoteContent(transcription.getQuote().getContent())
                .quoteAuthor(transcription.getQuote().getAuthor())
                .createdAt(transcription.getCreatedAt())
                .build();
    }
}
