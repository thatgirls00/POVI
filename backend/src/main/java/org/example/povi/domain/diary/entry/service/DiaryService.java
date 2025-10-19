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

    private static final int TITLE_MIN = 2;
    private static final int TITLE_MAX = 50;
    private static final int CONTENT_MIN = 10;
    private static final int CONTENT_MAX = 3000;
    private static final int IMG_MAX = 3;
    private static final int URL_MAX = 2048;

    private final DiaryRepository diaryRepository;
    private final UserRepository userRepository;

    @Transactional
    public DiaryCreateRes create(DiaryCreateReq req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        String title   = normalizeRequired(req.getTitle(), TITLE_MIN, TITLE_MAX, "제목");
        String content = normalizeRequired(req.getContent(), CONTENT_MIN, CONTENT_MAX, "내용");

        DiaryEntry diaryEntry = DiaryEntry.builder()
                .user(user)
                .title(title)
                .content(content)
                .moodEmoji(req.getMoodEmoji())
                .visibility(req.getVisibility())
                .build();

        // 이미지(생성)
        List<String> urls = normalizeImagesForCreate(req.getImageUrls());
        if (urls != null) {
            urls.forEach(u -> diaryEntry.addImage(new DiaryImage(diaryEntry, u)));
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

        // 제목
        if (req.getTitle() != null) {
            String title = normalizeRequired(req.getTitle(), TITLE_MIN, TITLE_MAX, "제목");
            diaryEntry.renameTo(title);
        }
        // 내용
        if (req.getContent() != null) {
            String content = normalizeRequired(req.getContent(), CONTENT_MIN, CONTENT_MAX, "내용");
            diaryEntry.rewriteContent(content);
        }
        // 이모지/공개범위
        if (req.getMoodEmoji() != null) diaryEntry.changeMood(req.getMoodEmoji());
        if (req.getVisibility() != null) diaryEntry.changeVisibility(req.getVisibility());

        // 이미지: null=미변경 / []=모두삭제 / 값있음=전체교체
        if (req.getImageUrls() != null) {
            List<String> normalized = normalizeImagesForPatch(req.getImageUrls());
            diaryEntry.replaceImages(normalized);
        }

        return DiaryUpdateRes.from(diaryEntry);
    }

    //공백 트림 후 필수값 + 길이검사 공통
    private String normalizeRequired(String raw, int min, int max, String label) {
        String v = trimToNull(raw);
        if (v == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, label + "은 공백만으로 생성/수정할 수 없습니다.");
        }
        if (v.length() < min || v.length() > max) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, label + "은 " + min + "~" + max + "자여야 합니다.");
        }
        return v;
    }

    //이미지 정규화(null=첨부없음 허용)
    private List<String> normalizeImagesForCreate(List<String> urls) {
        if (urls == null) return null;
        List<String> out = urls.stream()
                .filter(u -> u != null && !u.isBlank())
                .map(String::trim)
                .distinct()
                .toList();
        validateImageRules(out);
        return out;
    }

    //PATCH에서 사용할 이미지 정규화(null=미변경 / []=모두삭제)
    private List<String> normalizeImagesForPatch(List<String> urls) {
        List<String> out = urls.stream()
                .filter(u -> u != null && !u.isBlank())
                .map(String::trim)
                .distinct()
                .toList();
        validateImageRules(out);
        return out;
    }

    private void validateImageRules(List<String> urls) {
        if (urls.size() > IMG_MAX) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미지는 최대 " + IMG_MAX + "개까지 첨부할 수 있습니다.");
        }
        for (String u : urls) {
            if (u.length() > URL_MAX) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "이미지 URL은 " + URL_MAX + "자 이하여야 합니다.");
            }
        }
    }

    private String trimToNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}