package org.example.povi.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 로그인 요청 시 전달되는 이메일과 비밀번호를 담는 DTO.
 */
@Schema(description = "로그인 요청 DTO - 이메일과 비밀번호를 포함합니다.")
public record LoginRequestDto(

        @Schema(description = "사용자 이메일", example = "user@example.com")
        String email,

        @Schema(description = "사용자 비밀번호", example = "p@ssW0rd123")
        String password

) {}