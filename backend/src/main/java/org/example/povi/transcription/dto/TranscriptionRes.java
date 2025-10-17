package org.example.povi.transcription.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.povi.domain.Transcription;

import java.time.LocalDateTime;

@Getter
@Builder
public class TranscriptionRes {
    private final Long transcriptionId;
    private final String content;
    private final LocalDateTime createdAt;

    public static TranscriptionRes fromEntity(Transcription transcription) {
        return TranscriptionRes.builder()
                .transcriptionId(transcription.getId())
                .content(transcription.getContent())
                .createdAt(transcription.getCreatedAt())
                .build();
    }
}
