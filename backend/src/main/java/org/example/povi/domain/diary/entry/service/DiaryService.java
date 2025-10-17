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
    //추후 추가 예정 - 소유자 검증
    @Transactional
    public DiaryUpdateRes update(Long diaryId, DiaryUpdateReq req) {
        DiaryEntry diaryEntry = diaryRepository.findById(diaryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Diary not found"));

        String title = trimToNull(req.getTitle());
        String content = trimToNull(req.getContent());
        // 제목
        if (req.getTitle() != null) {
            if (title == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "제목은 공백만으로 수정할 수 없습니다.");
            if (title.length() < 2 || title.length() > 50)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "제목은 2~50자여야 합니다.");
            diaryEntry.renameTo(title);
        }

        // 내용
        if (req.getContent() != null) {
            if (content == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "내용은 공백만으로 수정할 수 없습니다.");
            if (content.length() < 10 || content.length() > 3000)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "내용은 10~3000자여야 합니다.");
            diaryEntry.rewriteContent(content);
        }

        // 이모지/공개범위
        if (req.getMoodEmoji() != null) diaryEntry.changeMood(req.getMoodEmoji());
        if (req.getVisibility() != null) diaryEntry.changeVisibility(req.getVisibility());

        // 이미지: null=미변경 / []=모두삭제 / 값있음=전체교체
        if (req.getImageUrls() != null) {
            List<String> normalized = req.getImageUrls().stream()
                    .filter(u -> u != null && !u.isBlank())
                    .map(String::trim)
                    .distinct()
                    .toList();

            if (normalized.size() > 3) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미지는 최대 3개까지 첨부할 수 있습니다.");
            }
            for (String u : normalized) {
                if (u.length() > 2048) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미지 URL은 2048자 이하여야 합니다.");
                }
            }

            diaryEntry.replaceImages(normalized);
        }
        diaryRepository.flush();

        return DiaryUpdateRes.from(diaryEntry);
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}