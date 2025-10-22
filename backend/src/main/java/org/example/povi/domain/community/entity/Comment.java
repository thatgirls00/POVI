package org.example.povi.domain.community.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.povi.domain.user.entity.User;
import org.example.povi.global.entity.BaseEntity;

@Entity
@Table(name = "comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {
    @Column(nullable = false, length = 1500)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private CommunityPost communityPost;

    @Column(nullable = false)
    private int likeCount = 0;

    @Builder
    public Comment(String content, User user, CommunityPost communityPost) {
        this.content = content;
        this.user = user;
        this.communityPost = communityPost;
    }

    public void addLike() {
        this.likeCount++;
    }

    public void removeLike() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
}
