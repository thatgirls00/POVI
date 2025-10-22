package org.example.povi.domain.user.dto;

import org.example.povi.domain.transcription.dto.TranscriptionListRes;

public record MyPageRes(ProfileRes profileRes,
                        long diaryCount,
                        TranscriptionListRes transcriptionListRes
) {

    public static MyPageRes of(ProfileRes profile, long diaryCount, TranscriptionListRes transcriptions) {
        return new MyPageRes(profile, diaryCount, transcriptions);
    }
}