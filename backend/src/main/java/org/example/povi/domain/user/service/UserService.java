package org.example.povi.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.povi.domain.diary.entry.service.DiaryService;
import org.example.povi.domain.transcription.dto.TranscriptionListRes;
import org.example.povi.domain.transcription.service.TranscriptionService;
import org.example.povi.domain.user.dto.MyPageRes;
import org.example.povi.domain.user.entity.User;
import org.example.povi.domain.user.repository.UserRepository;
import org.example.povi.global.exception.ex.ResourceNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DiaryService diaryService;
    private final TranscriptionService transcriptionService;

    @Transactional
    public MyPageRes getMyPage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        long diaryCount = diaryService.getDiaryCountForUser(userId);

        Pageable previewPageable = PageRequest.of(0, 4);
        TranscriptionListRes transcriptionList = transcriptionService.getMyTranscriptions(userId, previewPageable);
        return MyPageRes.fromEntity(user, diaryCount, transcriptionList);
    }
}
