package org.example.povi.domain.user.follow.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.povi.domain.user.entity.User;
import org.example.povi.global.entity.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "follows",
        uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "following_id"}))
@AttributeOverride(name = "id", column = @Column(name = "follow_id"))
public class Follow extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "following_id", nullable = false)
    private User following;

    @Column(name = "followed_at", nullable = false)
    private LocalDateTime followedAt;

    public Follow(User follower, User following) {
        this.follower = follower;
        this.following = following;
        this.followedAt = LocalDateTime.now();
    }
}