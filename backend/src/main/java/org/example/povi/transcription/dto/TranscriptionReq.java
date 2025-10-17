package org.example.povi.transcription.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TranscriptionReq {

    @NotBlank(message = "필사 내용은 비어있을 수 없습니다.")
    private String content;
}
