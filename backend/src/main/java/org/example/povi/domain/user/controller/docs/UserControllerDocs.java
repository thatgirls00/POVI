package org.example.povi.domain.user.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.example.povi.domain.user.dto.MyPageRes;
import org.example.povi.domain.user.dto.ProfileRes;
import org.example.povi.domain.user.dto.ProfileUpdateReq;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "사용자 API", description = "사용자의 마이페이지 조회 및 프로필 수정 기능 제공")
public interface UserControllerDocs {

    @Operation(
            summary = "마이페이지 조회",
            description = "현재 로그인한 사용자의 마이페이지 정보를 조회합니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "액세스 토큰", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "조회 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = MyPageRes.class)))
            }
    )
    ResponseEntity<?> getMyPage(@RequestHeader("Authorization") String bearerToken);

    @Operation(
            summary = "프로필 수정",
            description = "닉네임, 자기소개, 프로필 이미지를 수정합니다.",
            parameters = {
                    @Parameter(name = "Authorization", description = "액세스 토큰", required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "수정 성공",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ProfileRes.class)))
            }
    )
    ResponseEntity<?> updateProfile(
            @RequestHeader("Authorization") String bearerToken,
            @RequestPart("dto") ProfileUpdateReq reqDto,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    );

}
