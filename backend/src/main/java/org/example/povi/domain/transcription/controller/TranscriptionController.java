package org.example.povi.domain.transcription.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.povi.auth.token.jwt.JwtTokenProvider;
import org.example.povi.domain.transcription.controller.docs.TranscriptionControllerDocs;
import org.example.povi.domain.transcription.dto.TranscriptionReq;
import org.example.povi.domain.transcription.service.TranscriptionService;
import org.example.povi.domain.transcription.dto.TranscriptionListRes;
import org.example.povi.domain.transcription.dto.TranscriptionRes;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/transcriptions")
@RequiredArgsConstructor
public class TranscriptionController implements TranscriptionControllerDocs {

    private final TranscriptionService transcriptionService;
    private final JwtTokenProvider jwtTokenProvider;

    private String resolveToken(String bearerToken) {
        return bearerToken.replace("Bearer ", "");
    }

    @PostMapping("/{quoteId}")
    public ResponseEntity<TranscriptionRes> createTranscription(
            @PathVariable Long quoteId,
            @Valid @RequestBody TranscriptionReq reqDto,
            @RequestHeader("Authorization") String bearerToken
            ) {
        String token = resolveToken(bearerToken);
        Long userId = jwtTokenProvider.getUserId(token);
        TranscriptionRes responseDto = transcriptionService.createTranscription(userId, quoteId, reqDto);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{transcriptionId}")
    public ResponseEntity<?> deleteTranscription(
            @PathVariable Long transcriptionId,
            @RequestHeader("Authorization") String bearerToken
    ) {
        String token = resolveToken(bearerToken);
        Long userId = jwtTokenProvider.getUserId(token);
        transcriptionService.deleteTranscription(userId, transcriptionId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")  // 본인이 작성한 필사기록 조회
    public ResponseEntity<TranscriptionListRes> getMyTranscriptions(
            @RequestHeader("Authorization") String bearerToken,
            @PageableDefault(size = 4, sort = "createdAt", direction = Sort.Direction.DESC)Pageable pageable) {
        String token = resolveToken(bearerToken);
        Long userId = jwtTokenProvider.getUserId(token);
        TranscriptionListRes responseDto = transcriptionService.getMyTranscriptions(userId,pageable);
        return ResponseEntity.ok(responseDto);
    }
}
