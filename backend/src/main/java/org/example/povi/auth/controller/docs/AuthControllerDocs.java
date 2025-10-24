package org.example.povi.auth.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.povi.auth.dto.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "인증 API", description = "회원가입, 로그인, 토큰 재발급, 로그아웃 등 인증 관련 기능 제공")
public interface AuthControllerDocs {

    @Operation(summary = "회원가입", description = "이메일 기반 회원가입을 수행합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "유효성 검사 실패", content = @Content)
    })
    ResponseEntity<Void> signup(@RequestBody SignupRequestDto requestDto);

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 Access/Refresh 토큰을 발급받습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto requestDto);

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공", content = @Content(schema = @Schema(implementation = MeResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    ResponseEntity<MeResponseDto> getMyInfo();

    @Operation(summary = "토큰 재발급", description = "Refresh Token을 이용하여 Access Token을 재발급받습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공", content = @Content(schema = @Schema(implementation = TokenReissueResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패 또는 만료된 토큰", content = @Content)
    })
    ResponseEntity<TokenReissueResponseDto> reissueAccessToken(@RequestBody TokenReissueRequestDto requestDto);

    @Operation(summary = "로그아웃", description = "현재 로그인한 사용자의 토큰을 만료 처리하여 로그아웃합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패", content = @Content)
    })
    ResponseEntity<Void> logout();

    @Operation(summary = "OAuth2 로그인 콜백 처리", description = "Google/Kakao 소셜 로그인 이후 콜백으로 accessToken, refreshToken을 전달받아 응답합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "소셜 로그인 완료", content = @Content(schema = @Schema(implementation = TokenReissueResponseDto.class)))
    })
    ResponseEntity<TokenReissueResponseDto> oauthCallback(
            @Parameter(description = "OAuth Provider 이름 (예: google, kakao)") String provider,
            @Parameter(description = "Access Token") @RequestParam("accessToken") String accessToken,
            @Parameter(description = "Refresh Token") @RequestParam("refreshToken") String refreshToken);

    @Operation(summary = "회원 탈퇴", description = "로그인한 사용자의 계정을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "401", description = "JWT 토큰이 없거나 유효하지 않음", content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류", content = @Content)
    })
    ResponseEntity<Void> withdraw();
}