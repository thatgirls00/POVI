package org.example.povi.domain.quote.controller;

import lombok.RequiredArgsConstructor;
import org.example.povi.domain.quote.service.QuoteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("quotes")
@RequiredArgsConstructor
public class QuoteController {

    private final QuoteService quoteService;

    @GetMapping("/today")
    public ResponseEntity<?> getTodayQuote() {
        return quoteService.getTodayQuote()
                .map(ResponseEntity::ok) // 값이 있으면 200 OK와 함께 데이터 반환
                .orElseGet(() -> ResponseEntity.notFound().build()); // 값이 없으면 404 Not Found 반환
    }
}
