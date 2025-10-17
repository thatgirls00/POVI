package org.example.povi.transcription.controller;

import lombok.RequiredArgsConstructor;
import org.example.povi.transcription.dto.TranscriptionReq;
import org.example.povi.transcription.service.TranscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transcriptions")
@RequiredArgsConstructor
public class TranscriptionController {

    private final TranscriptionService transcriptionService;

    @PostMapping("/{quoteId}")
    public ResponseEntity<?> createTranscription(
            @PathVariable Long quoteId,
            @RequestBody TranscriptionReq reqDto,
            @RequestParam Long userId
            ) {
        transcriptionService.createTranscription(userId, quoteId, reqDto);

        return ResponseEntity.status(HttpStatus.CREATED).body("필사 기록이 성공적으로 생성됐습니다.");
    }
}
