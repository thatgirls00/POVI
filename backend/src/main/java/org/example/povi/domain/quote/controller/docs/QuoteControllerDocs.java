package org.example.povi.domain.quote.controller.docs;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "명언 API", description = "오늘의 명언을 랜덤으로 조회하는 기능 제공")
public interface QuoteControllerDocs {

    @Operation(
            summary = "오늘의 명언 조회",
            description = "랜덤으로 오늘의 명언 한 개를 조회합니다.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "성공적으로 명언을 조회한 경우"),
                    @ApiResponse(responseCode = "404", description = "명언이 존재하지 않는 경우")
            }
    )
    @GetMapping("/today")
    ResponseEntity<?> getTodayQuote();
}