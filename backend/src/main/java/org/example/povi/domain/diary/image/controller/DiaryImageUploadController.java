package org.example.povi.domain.diary.image.controller;

import lombok.RequiredArgsConstructor;
import org.example.povi.domain.diary.image.service.DiaryImageUploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diary-images")
public class DiaryImageUploadController {

    private final DiaryImageUploadService diaryImageUploadService;

    /**
     * 다이어리 이미지 업로드
     */
    @PostMapping
    public ResponseEntity<List<String>> uploadDiaryImages(
            @RequestPart("images") List<MultipartFile> images
    ) {
        List<String> uploadedUrls = diaryImageUploadService.upload(images);
        return ResponseEntity.ok(uploadedUrls);
    }

    /**
     * 다이어리 이미지 삭제
     */
    @DeleteMapping
    public ResponseEntity<Void> deleteDiaryImage(
            @RequestParam("imageUrl") String imageUrl
    ) {
        diaryImageUploadService.deleteByUrl(imageUrl);
        return ResponseEntity.noContent().build();
    }
}
