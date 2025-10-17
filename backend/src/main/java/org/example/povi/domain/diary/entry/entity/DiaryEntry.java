package org.example.povi.domain.diary.entry.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.povi.domain.diary.entry.dto.request.DiaryUpdateReq;
import org.example.povi.domain.diary.type.MoodEmoji;
import org.example.povi.domain.diary.type.Visibility;
import org.example.povi.domain.user.entity.User;
import org.example.povi.global.entity.BaseEntity;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "diary_entries")
@AttributeOverride(name = "id", column = @Column(name = "diary_id"))
public class DiaryEntry extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "title", length = 255, nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "mood_emoji", nullable = false)
    private MoodEmoji moodEmoji = MoodEmoji.NEUTRAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private Visibility visibility = Visibility.PRIVATE;

    @OneToMany(mappedBy = "diaryEntry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiaryImage> images = new ArrayList<>();

    @Builder
    public DiaryEntry(User user, String title, String content, MoodEmoji moodEmoji, Visibility visibility) {

        this.user = user;
        this.title = title;
        this.content = content;
        this.moodEmoji = (moodEmoji != null) ? moodEmoji : MoodEmoji.NEUTRAL;
        this.visibility = (visibility != null) ? visibility : Visibility.PRIVATE;
    }

    public void addImage(DiaryImage image) {
        images.add(image);
        image.setDiaryEntry(this);
    }

    public void update(DiaryUpdateReq req) {
        if (req.getTitle() != null) this.title = req.getTitle();
        if (req.getContent() != null) this.content = req.getContent();
        if (req.getMoodEmoji() != null) this.moodEmoji = req.getMoodEmoji();
        if (req.getVisibility() != null) this.visibility = req.getVisibility();

        if (req.getImageUrls() != null) {
            this.images.clear();
            req.getImageUrls().forEach(url -> this.addImage(new DiaryImage(this, url)));
        }
    }

}

