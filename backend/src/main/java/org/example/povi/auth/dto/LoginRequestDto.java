package org.example.povi.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * 로그인 요청 시 전달되는 이메일과 비밀번호를 담는 DTO.
 */
public record LoginRequestDto(

        @NotBlank(message = "이메일은 비워둘 수 없습니다.")
        @Email(message = "올바른 이메일 형식을 입력해주세요.")
        String email,

        @NotBlank(message = "비밀번호는 비워둘 수 없습니다.")
        @Size(min = 8, max = 64, message = "비밀번호는 8자 이상 64자 이하로 입력해주세요.")
        String password

) {}