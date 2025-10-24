package org.example.povi.domain.transcription.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.povi.domain.transcription.dto.TranscriptionListRes;
import org.example.povi.domain.transcription.dto.TranscriptionReq;
import org.example.povi.domain.transcription.dto.TranscriptionRes;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "명언 필사 API", description = "명언에 대한 필사 작성, 삭제, 조회 기능 제공")
public interface TranscriptionControllerDocs {

    @Operation(summary = "명언 필사 작성", description = "특정 명언에 대한 필사 기록을 생성합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "작성 성공",
                            content = @Content(schema = @Schema(implementation = TranscriptionRes.class)))
            })
    ResponseEntity<?> createTranscription(
            @Parameter(description = "명언 ID", example = "1") @PathVariable Long quoteId,
            @RequestBody TranscriptionReq reqDto,
            @RequestHeader("Authorization") String bearerToken
    );

    @Operation(summary = "명언 필사 삭제", description = "작성한 필사 기록을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "삭제 성공")
            })
    ResponseEntity<?> deleteTranscription(
            @Parameter(description = "필사 ID", example = "3") @PathVariable Long transcriptionId,
            @RequestHeader("Authorization") String bearerToken
    );

    @Operation(summary = "내 필사 기록 조회", description = "사용자가 작성한 필사 기록 목록을 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(schema = @Schema(implementation = TranscriptionListRes.class)))
            })
    ResponseEntity<?> getMyTranscriptions(
            @RequestHeader("Authorization") String bearerToken,
            @Parameter(description = "페이지 번호", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지 크기", example = "20") @RequestParam(defaultValue = "20") int size
    );
}