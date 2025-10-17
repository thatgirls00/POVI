package org.example.povi.transcription.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.povi.transcription.dto.TranscriptionReq;
import org.example.povi.transcription.dto.TranscriptionListRes;
import org.example.povi.transcription.dto.TranscriptionRes;
import org.example.povi.transcription.service.TranscriptionService;
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
            @RequestBody @Valid TranscriptionReq reqDto,
            @RequestParam Long userId
            ) {
        TranscriptionRes responseDto = transcriptionService.createTranscription(userId, quoteId, reqDto);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{transcriptionId}")
    public ResponseEntity<?> deleteTranscription(
            @PathVariable Long transcriptionId,
            @RequestParam Long userId
    ) {
        transcriptionService.deleteTranscription(userId, transcriptionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")  // 본인이 작성한 필사기록 조회
    public ResponseEntity<?> getMyTranscriptions(@RequestParam Long userId) {
        TranscriptionListRes responseDto = transcriptionService.getMyTranscriptions(userId);
        return ResponseEntity.ok(responseDto);
    }
}
