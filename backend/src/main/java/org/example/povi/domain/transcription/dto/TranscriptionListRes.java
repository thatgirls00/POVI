package org.example.povi.domain.transcription.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.List;

public record TranscriptionListRes(List<TranscriptionDetail> transcriptionList) {
}
