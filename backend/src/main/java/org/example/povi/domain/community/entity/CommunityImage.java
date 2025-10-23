package org.example.povi.domain.community.entity;

import jakarta.persistence.AttributeOverride;
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
import org.example.povi.global.entity.BaseEntity;

@Entity
@Table(name = "community_images")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(
        name = "id",
        column = @Column(name = "image_id")
)
public class CommunityImage extends BaseEntity {

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    // 이 이미지가 속한 게시글 (N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    private CommunityPost communityPost;

    @Builder
    public CommunityImage(String imageUrl, CommunityPost communityPost) {
        this.imageUrl = imageUrl;
        this.communityPost = communityPost;
    }

}
