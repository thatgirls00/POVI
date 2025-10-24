package org.example.povi.domain.quote.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.example.povi.domain.quote.entity.Quote;

@Schema(description = "명언 응답 DTO")
public record QuoteRes(

        @Schema(description = "명언 ID", example = "1")
        Long quoteId,

        @Schema(description = "작성자", example = "파울로 코엘료")
        String author,

        @Schema(description = "명언 메시지", example = "당신이 무언가를 간절히 원할 때, 온 우주는 그것을 이루기 위해 움직인다.")
        String message

) {
    public static QuoteRes fromEntity(Quote quote) {
        return new QuoteRes(
                quote.getId(),
                quote.getAuthor(),
                quote.getContent()
        );
    }
}