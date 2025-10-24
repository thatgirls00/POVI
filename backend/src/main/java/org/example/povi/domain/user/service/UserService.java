package org.example.povi.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.povi.domain.diary.post.service.DiaryPostService;
import org.example.povi.domain.transcription.dto.TranscriptionListRes;
import org.example.povi.domain.transcription.service.TranscriptionService;
import org.example.povi.domain.user.dto.MyPageRes;
import org.example.povi.domain.user.dto.ProfileRes;
import org.example.povi.domain.user.dto.ProfileUpdateReq;
import org.example.povi.domain.user.entity.User;
import org.example.povi.domain.user.repository.UserRepository;
import org.example.povi.global.exception.ex.ResourceNotFoundException;
import org.example.povi.global.mapper.UserMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final DiaryPostService diaryPostService;
    private final TranscriptionService transcriptionService;
    private final FileStorageService fileStorageService;
    private final UserMapper userMapper;

    @Transactional(readOnly = true) // 마이페이지 조회
    public MyPageRes getMyPage(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));
        ProfileRes profileRes = userMapper.toProfileRes(user);

        long diaryCount = diaryPostService.getDiaryPostCountForUser(userId);
        Pageable previewPageable = PageRequest.of(0, 4);
        TranscriptionListRes transcriptionList = transcriptionService.getMyTranscriptions(userId, previewPageable);

        return MyPageRes.of(profileRes, diaryCount, transcriptionList);
    }

    @Transactional // 프로필 수정
    public ProfileRes updateProfile(Long userId, ProfileUpdateReq reqDto, MultipartFile image) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        userMapper.updateUserFromDto(user, reqDto);

        if (image != null && !image.isEmpty()) {
            String imageUrl = fileStorageService.storeFile(image);
            user.updateProfileImgUrl(imageUrl);
        }

        return userMapper.toProfileRes(user);
    }
}
