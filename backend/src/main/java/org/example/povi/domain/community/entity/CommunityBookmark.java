package org.example.povi.domain.community.entity;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.povi.domain.user.entity.User;
import org.example.povi.global.entity.BaseEntity;

@Entity
@Table(name = "bookmarks",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "bookmark_uk",
                        columnNames = {"user_id", "post_id"}
                )
        }
)
@AttributeOverride(
        name = "id",
        column = @Column(name = "bookmark_id")
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityBookmark extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private CommunityPost communityPost;

    @Builder
    public CommunityBookmark(User user, CommunityPost communityPost) {
        this.user = user;
        this.communityPost = communityPost;
    }

}
