package org.example.povi.domain.transcription.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "사용자 필사 목록 응답 DTO")
public record TranscriptionListRes(

        @Schema(description = "필사 상세 목록")
        List<TranscriptionDetail> transcriptionList,

        @Schema(description = "전체 필사 개수")
        long totalCount
) {}