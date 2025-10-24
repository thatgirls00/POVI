// Swagger 전용 인터페이스
package org.example.povi.auth.email.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.example.povi.auth.email.dto.EmailVerificationRequestDto;
import org.example.povi.auth.email.dto.EmailVerificationStatusResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "이메일 인증 API", description = "이메일 인증 요청, 검증, 상태 조회 API")
public interface EmailVerificationControllerDocs {

    @Operation(summary = "이메일 인증 메일 전송", description = "입력된 이메일 주소로 인증 메일을 전송합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "인증 메일 전송 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청", content = @Content)
    })
    @PostMapping("/send")
    ResponseEntity<Void> sendEmailVerification(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "이메일 인증 요청 DTO", required = true,
                    content = @Content(schema = @Schema(implementation = EmailVerificationRequestDto.class)))
            @Valid @RequestBody EmailVerificationRequestDto request
    );

    @Operation(summary = "이메일 인증 토큰 검증", description = "토큰을 이용해 이메일 인증 여부를 검증합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 인증 성공"),
            @ApiResponse(responseCode = "400", description = "이메일 인증 실패", content = @Content)
    })
    @GetMapping("/verify")
    ResponseEntity<Void> verifyEmail(
            @Parameter(description = "이메일 인증 토큰") @RequestParam("token") String token
    );

    @Operation(summary = "이메일 인증 여부 조회", description = "이메일 주소의 인증 완료 여부를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "이메일 인증 상태 조회 성공",
                    content = @Content(schema = @Schema(implementation = EmailVerificationStatusResponseDto.class)))
    })
    @GetMapping("/status")
    ResponseEntity<EmailVerificationStatusResponseDto> checkEmailVerificationStatus(
            @Parameter(description = "이메일 주소") @RequestParam String email
    );
}