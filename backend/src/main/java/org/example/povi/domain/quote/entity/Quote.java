package org.example.povi.domain.quote.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.povi.global.entity.BaseEntity;
import org.example.povi.domain.transcription.entity.Transcription;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "quote_id"))
@Table(name = "quotes")
public class Quote extends BaseEntity {
    @Column(nullable = false)
    private String author;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DataSource dataSource = DataSource.GITHUB;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuoteSource quoteSource = QuoteSource.QUOTE;

    @OneToMany(mappedBy = "quote", cascade = CascadeType.ALL)
    private List<Transcription> transcriptions = new ArrayList<>();

    public Quote(String author, String content) {
        this.author = author;
        this.content = content;
    }

    public enum DataSource {
        GITHUB,
        AI
    }

    public enum QuoteSource {
        QUOTE,  // 명언
        BOOK,    // 책구절
        MOVIE  // 영화
    }

}
