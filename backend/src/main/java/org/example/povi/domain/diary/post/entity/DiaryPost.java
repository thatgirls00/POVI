package org.example.povi.domain.diary.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.povi.domain.diary.comment.entity.DiaryComment;
import org.example.povi.domain.diary.enums.MoodEmoji;
import org.example.povi.domain.diary.enums.Visibility;
import org.example.povi.domain.diary.like.entity.DiaryPostLike;
import org.example.povi.domain.user.entity.User;
import org.example.povi.global.entity.BaseEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "diary_posts")
@AttributeOverride(name = "id", column = @Column(name = "post_id"))
public class DiaryPost extends BaseEntity {

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "mood_emoji", nullable = false)
    private MoodEmoji moodEmoji = MoodEmoji.NEUTRAL;

    @Enumerated(EnumType.STRING)
    @Column(name = "visibility", nullable = false)
    private Visibility visibility = Visibility.PRIVATE;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id ASC")
    private List<DiaryImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<DiaryComment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Set<DiaryPostLike> likes = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private DiaryPost(User user, MoodEmoji moodEmoji, Visibility visibility) { // ← private
        this.user = user;
        this.moodEmoji = (moodEmoji != null) ? moodEmoji : MoodEmoji.NEUTRAL;
        this.visibility = (visibility != null) ? visibility : Visibility.PRIVATE;
    }

    public static DiaryPost create(User author, String title, String content,
                                   MoodEmoji mood, Visibility vis, List<String> imageUrls) {
        DiaryPost p = new DiaryPost(author, mood, vis);
        p.renameTo(title);
        p.rewriteContent(content);
        p.replaceImages(imageUrls);
        return p;
    }

    public void renameTo(String newTitle) {
        String t = (newTitle == null ? "" : newTitle.trim());
        if (t.isEmpty()) {
            throw new IllegalArgumentException("제목은 공백만으로 수정할 수 없습니다.");
        }
        this.title = t;
    }

    public void rewriteContent(String newContent) {
        String c = (newContent == null ? "" : newContent.trim());
        if (c.isEmpty()) {
            throw new IllegalArgumentException("내용은 공백만으로 수정할 수 없습니다.");
        }
        this.content = c;
    }

    public void changeMood(MoodEmoji newMood) {
        if (newMood == null) throw new IllegalArgumentException("이모지는 null 일 수 없습니다.");
        this.moodEmoji = newMood;
    }

    public void changeVisibility(Visibility newVisibility) {
        if (newVisibility == null) throw new IllegalArgumentException("공개범위는 null 일 수 없습니다.");
        this.visibility = newVisibility;
    }

    public void addImage(DiaryImage image) {
        if (image == null) return;
        images.add(image);
        if (image.getPost() != this) image.setDiaryPost(this);
    }

    public void replaceImages(List<String> urls) {
        images.clear();
        if (urls == null || urls.isEmpty()) return;

        List<String> sanitized = urls.stream()
                .map(u -> u == null ? "" : u.trim())
                .filter(s -> !s.isEmpty())
                .distinct()
                .toList();

        for (String url : sanitized) {
            addImage(new DiaryImage(this, url));
        }
    }
}

