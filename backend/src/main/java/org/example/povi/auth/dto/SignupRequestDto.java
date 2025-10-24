package org.example.povi.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 회원가입 요청을 위한 DTO.
 */
@Schema(description = "회원가입 요청을 위한 데이터 전송 객체")
public record SignupRequestDto(

        @Schema(description = "사용자 이메일", example = "user@example.com")
        @NotBlank(message = "이메일은 비워둘 수 없습니다.")
        @Email(message = "올바른 이메일 형식을 입력해주세요.")
        String email,

        @Schema(description = "사용자 비밀번호", example = "secureP@ssw0rd")
        @NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
        @Size(min = 8, max = 64, message = "비밀번호는 8자 이상 64자 이하로 입력해주세요.")
        String password,

        @Schema(description = "사용자 닉네임", example = "홍길동")
        @NotBlank(message = "닉네임은 비워둘 수 없습니다.")
        String nickname,

        @Schema(description = "OAuth 제공자 (ex: google, kakao)", example = "google")
        String provider,

        @Schema(description = "OAuth 제공자 고유 ID", example = "1037246634527")
        String providerId

) {}