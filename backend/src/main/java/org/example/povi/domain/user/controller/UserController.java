package org.example.povi.domain.user.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.povi.auth.token.jwt.CustomJwtUser;
import org.example.povi.domain.user.dto.MyPageRes;
import org.example.povi.domain.user.dto.ProfileRes;
import org.example.povi.domain.user.dto.ProfileUpdateReq;
import org.example.povi.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/myPage")
    public ResponseEntity<?> getMyPage(@AuthenticationPrincipal CustomJwtUser userDetails) {
        Long userId = userDetails.getId();
        MyPageRes responseDto = userService.getMyPage(userId);
        return ResponseEntity.ok(responseDto);
    }

    @PatchMapping("/updateProfile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal CustomJwtUser userDetails,
            @RequestPart("dto") @Valid ProfileUpdateReq reqDto,
            @RequestPart(value = "image", required = false)MultipartFile imageFile
            ) {
        Long userId = userDetails.getId();
        ProfileRes responseDto = userService.updateProfile(userId, reqDto, imageFile);
        return ResponseEntity.ok(responseDto);
    }

}
