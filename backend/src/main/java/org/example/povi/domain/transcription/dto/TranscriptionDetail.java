package org.example.povi.domain.transcription.dto;

import org.example.povi.domain.transcription.entity.Transcription;

import java.time.LocalDateTime;

public record TranscriptionDetail(
        Long transcriptionId,
        String content,
        String quoteContent,
        String quoteAuthor,
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
