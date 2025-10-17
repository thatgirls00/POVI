package org.example.povi.domain.transcription.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class TranscriptionListRes {
    private final List<TranscriptionDetail> transcriptionList;
}
