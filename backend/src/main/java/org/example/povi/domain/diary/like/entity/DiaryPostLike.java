package org.example.povi.domain.diary.like.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.example.povi.domain.user.entity.User;
import org.example.povi.global.entity.BaseEntity;

import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "diaryPost_likes")
@AttributeOverride(name = "id", column = @Column(name = "post_like_id"))
public class DiaryPostLike extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private DiaryPost post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "liked_at", nullable = false, updatable = false)
    private LocalDateTime likedAt;

    private DiaryPostLike(DiaryPost post, User user) {
        this.post = post;
        this.user = user;
        this.likedAt = LocalDateTime.now();
    }

    public static DiaryPostLike of(DiaryPost post, User user) {
        return new DiaryPostLike(post, user);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiaryPostLike that)) return false;
        return Objects.equals(getPost(), that.getPost()) &&
                Objects.equals(getUser(), that.getUser());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPost(), getUser());
    }
}