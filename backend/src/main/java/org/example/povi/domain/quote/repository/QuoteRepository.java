package org.example.povi.domain.quote.repository;

import org.example.povi.domain.quote.entity.Quote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface QuoteRepository extends JpaRepository<Quote,Long> {

    Optional<Quote> findFirstByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startOfDay, LocalDateTime endOfDay);
}
