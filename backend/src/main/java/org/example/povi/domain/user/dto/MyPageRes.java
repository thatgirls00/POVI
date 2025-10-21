package org.example.povi.domain.user.dto;

import org.example.povi.domain.transcription.dto.TranscriptionListRes;
import org.example.povi.domain.user.entity.User;

public record MyPageRes(
        String nickname,
        String profileImgUrl,
        String bio,
        long diaryCount,
        TranscriptionListRes transcriptionListRes
//        long followerCount,
//        long followingCount
        ) {

    public static MyPageRes fromEntity(User user, long diaryCount, TranscriptionListRes transcriptions) {
        return new MyPageRes(user.getNickname(), user.getProfileImgUrl(), user.getBio(), diaryCount, transcriptions);
    }

    // 팔로우 관련 사항이 정해지면 추가예정
//    public static MyPageProfileRes from(User user, long diaryCount, long followerCount, long followingCount) {
//        return new MyPageProfileRes(
//            user.getNickname(),
//            user.getProfileImgUrl(),
//            user.getBio(),
//            diaryCount,
//            followerCount,
//            followingCount
//        );
//    }
}
