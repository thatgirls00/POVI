package org.example.povi.domain.diary.post.mapper;

import org.example.povi.domain.diary.post.entity.DiaryPost;


public final class DiaryPreviewMapper {

    private DiaryPreviewMapper() {}

    // 첫 번째 이미지 URL (없으면 null)
    public static String firstImageUrl(DiaryPost diary) {
        return diary.getImages().isEmpty() ? null : diary.getImages().get(0).getImageUrl();
    }

    // 미리보기 텍스트 (공백 정리 + 길이 제한)
    public static String buildPreviewText(String content, int maxLength) {
        if (content == null || content.isBlank()) return "";
        String compact = content.replaceAll("\\s+", " ").trim();
        return compact.length() <= maxLength ? compact : compact.substring(0, maxLength) + "...";
    }
}
