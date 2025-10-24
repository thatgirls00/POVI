package org.example.povi.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ProfileUpdateReq(
        @NotBlank(message = "닉네임은 필수항목입니다.")
        @Size(min = 2, max = 50)
        String nickname,
        String bio
) {}
