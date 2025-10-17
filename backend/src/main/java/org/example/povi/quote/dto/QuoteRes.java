package org.example.povi.quote.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public class QuoteRes{
    private Long quoteId;
    private String author;
    private String message;
}
