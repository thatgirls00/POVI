package org.example.povi.domain.user.controller;

import lombok.RequiredArgsConstructor;
import org.example.povi.auth.token.jwt.CustomJwtUser;
import org.example.povi.domain.user.dto.MyPageRes;
import org.example.povi.domain.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/me")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/mypage")
    public ResponseEntity<?> getMyProfile(@AuthenticationPrincipal CustomJwtUser userDetails) {
        Long userId = userDetails.getId();
        MyPageRes responseDto = userService.getMyPage(userId);
        return ResponseEntity.ok(responseDto);
    }
}
