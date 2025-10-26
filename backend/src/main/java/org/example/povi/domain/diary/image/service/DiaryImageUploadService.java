package org.example.povi.domain.diary.image.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Service
public class DiaryImageUploadService {

    @Value("${file.upload.diary.dir}")
    private String uploadDir; // 예: /Users/you/povi-uploads

    /**
     * 다이어리용 이미지 업로드
     * - 로컬 폴더에 저장하고 /images/{uuid.ext} URL 목록 반환
     */
    public List<String> upload(List<MultipartFile> files) {
        List<String> imageUrls = new ArrayList<>();
        if (files == null || files.isEmpty()) return imageUrls;

        File directory = new File(uploadDir);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IllegalStateException("업로드 경로 생성 실패: " + uploadDir);
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;

            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("이미지 파일만 업로드할 수 있습니다.");
            }

            String extension = getExt(Objects.requireNonNull(file.getOriginalFilename(), "파일명 없음"));
            String stored = UUID.randomUUID() + "." + extension;

            try {
                file.transferTo(new File(fullPath(stored)));
                imageUrls.add("/images/diary/" + stored);
            } catch (IOException e) {
                log.error("Diary image upload failed: {}", e.getMessage());
                throw new RuntimeException("다이어리 이미지 업로드 실패", e);
            }
        }
        return imageUrls;
    }

    /**
     * 다이어리 이미지 삭제 (URL → 실제 파일 경로 변환 후 삭제)
     */
    public void deleteByUrl(String imageUrl) {
        String filename = imageUrl.substring(imageUrl.lastIndexOf('/') + 1);
        Path path = Paths.get(fullPath(filename));
        try {
            Files.deleteIfExists(path);
        } catch (IOException e) {
            log.error("Diary image delete failed: {}", e.getMessage());
            throw new RuntimeException("다이어리 이미지 삭제 실패", e);
        }
    }

    private String fullPath(String fileName) {
        return uploadDir + File.separator + fileName;
    }

    private String getExt(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex < 0) throw new IllegalArgumentException("확장자를 찾을 수 없습니다: " + fileName);
        return fileName.substring(dotIndex + 1);
    }
}
