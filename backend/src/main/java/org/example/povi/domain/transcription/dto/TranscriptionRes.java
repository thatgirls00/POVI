package org.example.povi.domain.transcription.dto;

import org.example.povi.domain.transcription.entity.Transcription;

import java.time.LocalDateTime;

public record TranscriptionRes(Long transcriptionId, String content, LocalDateTime createdAt) {
    public static TranscriptionRes fromEntity(Transcription transcription) {
        return new TranscriptionRes(
                transcription.getId(),
                transcription.getContent(),
                transcription.getCreatedAt()
        );
    }
}
