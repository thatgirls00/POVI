package org.example.povi.domain.user.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    
    // 파일 외부 저장 경로
    @Value("${file.upload.profile.dir}")
    private String uploadDir;

    public String storeFile(MultipartFile file) {
        // 파일의 고유한 이름 생성
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        
        // 외부 경로에 파일 저장
        Path targetLocation = Paths.get(uploadDir).resolve(fileName);
        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            throw new RuntimeException("파일을 저장할 수 없습니다.", ex);
        }

        return "http://localhost:8080/images/" + fileName;
    }
}