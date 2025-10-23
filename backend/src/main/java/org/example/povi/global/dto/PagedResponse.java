package org.example.povi.global.dto;

import java.util.List;

/**
 * 공통 페이지네이션 응답 DTO
 * - content: 현재 페이지의 데이터 목록
 * - pageNumber: 현재 페이지 번호 (0-based)
 * - pageSize: 페이지당 데이터 개수
 * - totalElements: 전체 데이터 개수
 * - totalPages: 전체 페이지 수
 * - isFirst: 첫 페이지 여부
 * - isLast: 마지막 페이지 여부
 */
public record PagedResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean isFirst,
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