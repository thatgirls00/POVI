package org.example.povi.domain.community.service;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
public class FileUploadService {

    @Value("${file.upload.dir}")
    private String uploadDir;

    public List<String> uploadFiles(List<MultipartFile> multipartFiles) {
        List<String> storedFilePaths = new ArrayList<>();
        for (MultipartFile file : multipartFiles) {
            if (file.isEmpty()) {
                continue;
            }

            try {
                // 고유한 파일 이름 생성
                String originalFilename = file.getOriginalFilename();
                String storedFilename = createStoredFilename(originalFilename);

                // 파일을 지정된 경로에 저장
                File uploadPath = new File(uploadDir);
                if (!uploadPath.exists()) {
                    uploadPath.mkdirs(); // 디렉토리가 없으면 생성
                }
                file.transferTo(new File(getFullPath(storedFilename)));

                // 저장된 파일 경로를 리스트에 추가 (웹 접근 경로)
                storedFilePaths.add("/images/" + storedFilename); // 예시 경로

            } catch (IOException e) {
                log.error("File upload failed: {}", e.getMessage());
                // 실제 프로덕션 코드에서는 더 정교한 예외 처리가 필요합니다.
                throw new RuntimeException("파일 업로드에 실패했습니다.", e);
            }
        }
        return storedFilePaths;
    }

    public void deleteFile(String fileUrl) {
        // 웹 경로에서 실제 파일 시스템 경로로 변환
        String filename = fileUrl.substring(fileUrl.lastIndexOf("/") + 1);
        Path filePath = Paths.get(getFullPath(filename));
        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.error("File deletion failed: {}", e.getMessage());
            throw new RuntimeException("파일 삭제에 실패했습니다.", e);
        }
    }

    private String getFullPath(String filename) {
        return uploadDir + File.separator + filename;
    }

    // 파일 이름이 겹치지 않도록 UUID를 붙여서 새로운 파일 이름 생성
    private String createStoredFilename(String originalFilename) {
        String ext = extractExt(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    // 확장자(ext) 추출
    private String extractExt(String originalFilename) {
        int pos = originalFilename.lastIndexOf(".");
        return originalFilename.substring(pos + 1);
    }

}
