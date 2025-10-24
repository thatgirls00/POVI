package org.example.povi.global.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

/**
 * 공통 페이지네이션 응답 DTO
 */
@Schema(description = "공통 페이지네이션 응답 DTO")
public record PagedResponse<T>(

        @Schema(description = "현재 페이지의 데이터 목록", example = "[ ... ]")
        List<T> content,

        @Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
        int pageNumber,

        @Schema(description = "페이지당 데이터 개수", example = "20")
        int pageSize,

        @Schema(description = "전체 데이터 개수", example = "100")
        long totalElements,

        @Schema(description = "전체 페이지 수", example = "5")
        int totalPages,

        @Schema(description = "첫 페이지 여부", example = "true")
        boolean isFirst,

        @Schema(description = "마지막 페이지 여부", example = "false")
        boolean isLast

) {
    public static <T> PagedResponse<T> of(
            List<T> content, int pageNumber, int pageSize,
            long totalElements, int totalPages,
            boolean isFirst, boolean isLast
    ) {
        return new PagedResponse<>(content, pageNumber, pageSize, totalElements, totalPages, isFirst, isLast);
    }
}