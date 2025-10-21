package org.example.povi.domain.diary.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.povi.global.entity.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "diary_images")
public class DiaryImage extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "diary_id", nullable = false)
    private DiaryPost diaryPost;

    @Column(name = "image_url", length = 2048, nullable = false)
    private String imageUrl;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    public DiaryImage(DiaryPost diaryPost, String imageUrl) {
        this.diaryPost = diaryPost;
        this.imageUrl = imageUrl;
    }
    void setDiaryPost(DiaryPost diaryPost) {
        this.diaryPost = diaryPost;
    }
}