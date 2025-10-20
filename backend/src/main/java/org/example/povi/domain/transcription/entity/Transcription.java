package org.example.povi.domain.transcription.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.povi.domain.quote.entity.Quote;
import org.example.povi.domain.user.entity.User;
import org.example.povi.global.entity.BaseEntity;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "id", column = @Column(name = "transcription_id"))
@Table(name="transcriptions",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_quote",
        columnNames = {"user_id","quote_id"}
        )
    }
)
public class Transcription extends BaseEntity {

    private String content;

    @ManyToOne
    @JoinColumn(name = "quote_id")
    private Quote quote;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Transcription(String content, Quote quote, User user) {
        this.content = content;
        this.quote = quote;
        this.user = user;
    }
}