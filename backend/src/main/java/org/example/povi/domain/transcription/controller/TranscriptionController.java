package org.example.povi.domain.transcription.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.povi.auth.token.jwt.CustomJwtUser;
import org.example.povi.domain.transcription.dto.TranscriptionReq;
import org.example.povi.domain.transcription.service.TranscriptionService;
import org.example.povi.domain.transcription.dto.TranscriptionListRes;
import org.example.povi.domain.transcription.dto.TranscriptionRes;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transcriptions")
@RequiredArgsConstructor
public class TranscriptionController {

    private final TranscriptionService transcriptionService;

    @PostMapping("/{quoteId}")
    public ResponseEntity<?> createTranscription(
            @PathVariable Long quoteId,
            @Valid @RequestBody TranscriptionReq reqDto,
            @AuthenticationPrincipal CustomJwtUser userDetails
            ) {
        Long userId = userDetails.getId();
        TranscriptionRes responseDto = transcriptionService.createTranscription(userId, quoteId, reqDto);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{transcriptionId}")
    public ResponseEntity<?> deleteTranscription(
            @PathVariable Long transcriptionId,
            @AuthenticationPrincipal CustomJwtUser userDetails
    ) {
        Long userId = userDetails.getId();
        transcriptionService.deleteTranscription(userId, transcriptionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")  // 본인이 작성한 필사기록 조회
    public ResponseEntity<?> getMyTranscriptions(@AuthenticationPrincipal CustomJwtUser userDetails
    ) {
        Long userId = userDetails.getId();
        TranscriptionListRes responseDto = transcriptionService.getMyTranscriptions(userId);
        return ResponseEntity.ok(responseDto);
    }
}
