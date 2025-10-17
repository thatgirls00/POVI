package org.example.povi.domain.diary.entry.service;

import lombok.RequiredArgsConstructor;
import org.example.povi.domain.diary.entry.dto.request.DiaryCreateReq;
import org.example.povi.domain.diary.entry.dto.request.DiaryUpdateReq;
import org.example.povi.domain.diary.entry.dto.response.DiaryCreateRes;
import org.example.povi.domain.diary.entry.dto.response.DiaryUpdateRes;
import org.example.povi.domain.diary.entry.entity.DiaryEntry;
import org.example.povi.domain.diary.entry.entity.DiaryImage;
import org.example.povi.domain.diary.entry.repository.DiaryRepository;
import org.example.povi.domain.user.entity.User;
import org.example.povi.domain.user.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    @Transactional
    public DiaryCreateRes create(DiaryCreateReq req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String title = (req.getTitle() != null) ? req.getTitle().trim() : null;
        String content = (req.getContent() != null) ? req.getContent().trim() : null;

        DiaryEntry diaryEntry = DiaryEntry.builder()
                .user(user)
                .title(title)
                .content(content)
                .moodEmoji(req.getMoodEmoji())
                .visibility(req.getVisibility())
                .build();

        // 이미지 유효성 검사
        List<String> urls = req.getImageUrls();
        if (urls != null && !urls.isEmpty()) {
            if (urls.size() > 3) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미지는 최대 3개까지 첨부할 수 있습니다.");
            }
            urls.stream()
                    .filter(u -> u != null && !u.isBlank())
                    .forEach(url -> diaryEntry.addImage(new DiaryImage(diaryEntry, url.trim())));
        }

        diaryRepository.save(diaryEntry);
        return DiaryCreateRes.from(diaryEntry);
    }

    //다이어리 수정
    @Transactional
    public DiaryUpdateRes update(Long diaryId, DiaryUpdateReq req) {
        DiaryEntry diaryEntry = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Diary not found"));

        String title = (req.getTitle() != null) ? req.getTitle().trim() : null;
        String content = (req.getContent() != null) ? req.getContent().trim() : null;


        diaryEntry.update(req);

        return DiaryUpdateRes.from(diaryEntry);
    }
}