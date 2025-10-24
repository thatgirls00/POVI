package org.example.povi.domain.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "사용자 프로필 응답 DTO")
public record ProfileRes(

        @Schema(description = "사용자 닉네임", example = "행복한코끼리")
        String nickname,

        @Schema(description = "프로필 이미지 URL", example = "https://example.com/images/profile.jpg")
        String profileImgUrl,

        @Schema(description = "자기소개 (bio)", example = "하루하루 성장하는 개발자입니다.")
        String bio

) {}