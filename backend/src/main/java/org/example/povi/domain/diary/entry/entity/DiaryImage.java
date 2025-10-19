package org.example.povi.domain.diary.entry.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.povi.global.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "diary_images")
public class DiaryImage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "diary_id", nullable = false)
    private DiaryEntry diaryEntry;

    @Column(name = "image_url", length = 2048, nullable = false)
    private String imageUrl;

    public DiaryImage(DiaryEntry diaryEntry, String imageUrl) {
        this.diaryEntry = diaryEntry;
        this.imageUrl = imageUrl;
    }

    void setDiaryEntry(DiaryEntry diaryEntry) {
        this.diaryEntry = diaryEntry;
    }
}
