package org.example.povi.domain.diary.comment.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.example.povi.domain.user.entity.User;
import org.example.povi.global.entity.BaseEntity;

@Entity
@NoArgsConstructor
@Table(name = "diary_comments")
@Getter
@AttributeOverride(name = "id", column = @Column(name = "comment_id"))
public class DiaryComment extends BaseEntity {

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "post_id", nullable = false)
    private DiaryPost diaryPost;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;


    @Builder
    public DiaryComment(User author, DiaryPost diaryPost, String content) {
        this.author = author;
        this.diaryPost = diaryPost;
        this.content = content;

    }
}


