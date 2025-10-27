package org.example.povi.domain.quote.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.povi.domain.quote.dto.QuoteDto;
import org.example.povi.domain.quote.dto.QuoteRes;
import org.example.povi.domain.quote.entity.Quote;
import org.example.povi.domain.quote.repository.QuoteRepository;
import org.example.povi.global.exception.ex.QuoteFetchFailedException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class QuoteService {

    private final QuoteRepository quoteRepository;
    private final RestTemplate restTemplate;
    private static final String QUOTE_API_URL = "https://korean-advice-open-api.vercel.app/api/advice";

    public void getQuote() {    // 명언 api 호출
        try {
            QuoteDto quoteDto = restTemplate.getForObject(QUOTE_API_URL, QuoteDto.class);

            String author = quoteDto.getAuthor() + " " + quoteDto.getAuthorProfile();  // 발언자 + 발언자 소개를 합침
            String message = quoteDto.getMessage();

            if(message != null && !message.isEmpty()) {
                Quote quote = new Quote(author, message);
                quoteRepository.save(quote);
            }

        } catch (RestClientException e) {
            throw new QuoteFetchFailedException("외부 명언 API 호출에 실패했습니다.", e);
        }
    }

    public Optional<QuoteRes> getTodayQuote() {
        // 오늘의 시작과 끝 시간 설정
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay(); // 오늘 00:00:00
        LocalDateTime endOfDay = LocalDateTime.of(LocalDate.now(), LocalTime.MAX); // 오늘 23:59:59.99...

        // Repository를 통해 DB에서 오늘의 명언 조회
        Optional<Quote> optionalQuote = quoteRepository.findFirstByCreatedAtBetweenOrderByCreatedAtDesc(startOfDay, endOfDay);

        // Entity를 DTO로 변환하여 반환
        return optionalQuote.map(QuoteRes::fromEntity);
    }
}
