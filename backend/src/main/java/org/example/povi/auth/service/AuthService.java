package org.example.povi.auth.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.povi.auth.dto.*;
import org.example.povi.auth.enums.AuthProvider;
import org.example.povi.auth.mapper.UserMapper;
import org.example.povi.auth.token.jwt.JwtTokenProvider;
import org.example.povi.auth.token.jwt.RefreshTokenService;
import org.example.povi.domain.community.repository.*;
import org.example.povi.domain.diary.comment.repository.DiaryCommentRepository;
import org.example.povi.domain.diary.post.repository.DiaryPostRepository;
import org.example.povi.domain.mission.repository.UserMissionRepository;
import org.example.povi.domain.transcription.repository.TranscriptionRepository;
import org.example.povi.domain.user.entity.User;
import org.example.povi.domain.user.follow.repository.FollowRepository;
import org.example.povi.domain.user.repository.UserRepository;
import org.example.povi.global.exception.error.ErrorCode;
import org.example.povi.global.exception.ex.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    private final PostLikeRepository postLikeRepository;
    private final CommunityBookmarkRepository bookmarkRepository;
    private final CommentRepository commentRepository;
    private final DiaryCommentRepository diaryCommentRepository;
    private final TranscriptionRepository transcriptionRepository;
    private final UserMissionRepository userMissionRepository;
    private final FollowRepository followRepository;

    private final DiaryPostRepository diaryPostRepository;
    private final CommunityRepository communityRepository;

    /**
     * 회원가입 처리
     */
    public void signup(SignupRequestDto requestDto) {
        if (userRepository.existsByEmail(requestDto.email())) {
            throw new UserAlreadyExistsException();
        }

        AuthProvider provider;
        try {
            provider = AuthProvider.valueOf(requestDto.provider().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            throw new CustomException(ErrorCode.INVALID_AUTH_PROVIDER);
        }

        userRepository.findByEmail(requestDto.email()).ifPresent(existingUser -> {
            if (!existingUser.isEmailVerified()) {
                throw new CustomException(ErrorCode.EMAIL_NOT_VERIFIED);
            }
        });

        if (provider == AuthProvider.LOCAL && (requestDto.password() == null || requestDto.password().isBlank())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        User user = UserMapper.toEntity(requestDto, provider, passwordEncoder);
        userRepository.save(user);
    }

    /**
     * 로그인 처리
     */
    public LoginResponseDto login(LoginRequestDto requestDto) {
        User user = userRepository.findByEmail(requestDto.email())
                .orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(requestDto.password(), user.getPassword())) {
            throw new InvalidPasswordException();
        }

        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());
        refreshTokenService.save(user.getEmail(), refreshToken);

        return new LoginResponseDto(accessToken, refreshToken, user.getNickname());
    }

    /**
     * 로그아웃 처리
     */
    public void logout(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        refreshTokenService.delete(user.getEmail());
    }

    /**
     * 회원 탈퇴 처리
     */
    @Transactional
    public void withdraw(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 하위 엔티티 수동 삭제
        postLikeRepository.deleteAllByUser(user);
        bookmarkRepository.deleteAllByUser(user);
        commentRepository.deleteAllByUser(user);
        diaryCommentRepository.deleteAllByAuthor(user);
        transcriptionRepository.deleteAllByUser(user);
        userMissionRepository.deleteAllByUser(user);
        followRepository.deleteAllByFollowerOrFollowing(user, user);

        // cascade 설정된 엔티티 삭제
        diaryPostRepository.deleteAllByUser(user);
        communityRepository.deleteAllByUser(user);

        // 사용자 최종 삭제
        userRepository.delete(user);
    }
}