package org.example.povi.domain.quote.dto;

import org.example.povi.domain.quote.entity.Quote;

public record QuoteRes(Long quoteId, String author, String message) {
    public static QuoteRes fromEntity(Quote quote) {
        return new QuoteRes(
                quote.getId(),
                quote.getAuthor(),
                quote.getContent()
        );
    }
}
