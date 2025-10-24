package org.example.povi.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.povi.domain.transcription.dto.TranscriptionListRes;

@Schema(description = "마이페이지 응답 DTO")
public record MyPageRes(

        @Schema(description = "사용자 프로필 정보")
        ProfileRes profileRes,

        @Schema(description = "사용자가 작성한 일기 개수", example = "12")
        long diaryCount,

        @Schema(description = "사용자가 작성한 필사 목록")
        TranscriptionListRes transcriptionListRes

) {
    public static MyPageRes of(ProfileRes profile, long diaryCount, TranscriptionListRes transcriptions) {
        return new MyPageRes(profile, diaryCount, transcriptions);
    }
}