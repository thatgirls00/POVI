package org.example.povi.auth.email.limiter;

import lombok.RequiredArgsConstructor;
import org.example.povi.global.exception.ex.RateLimitExceededException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class EmailVerificationRateLimiter {

    private static final int MAX_DAILY_LIMIT = 5;
    private static final long TTL_HOURS = 24;

    private final StringRedisTemplate redisTemplate;

    public void validateSendLimit(String email) {
        String key = buildKey(email);
        Long count = redisTemplate.opsForValue().increment(key);

        if (count == 1) {
            redisTemplate.expire(key, TTL_HOURS, TimeUnit.HOURS);
        }

        if (count > MAX_DAILY_LIMIT) {
            throw new RateLimitExceededException("하루에 최대 5회까지만 인증 메일을 전송할 수 있습니다.");
        }
    }

    private String buildKey(String email) {
        String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        return "email:count:" + email + ":" + today;
    }
}