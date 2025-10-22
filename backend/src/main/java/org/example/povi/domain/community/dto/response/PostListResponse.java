package org.example.povi.domain.community.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import org.example.povi.domain.community.entity.CommunityPost;

public record PostListResponse(
        @Schema(description = "게시글 ID", example = "101")
        Long postId,

        @Schema(description = "게시글 제목", example = "오늘 날씨가 정말 좋네요!")
        String title,

        @Schema(description = "작성자 닉네임", example = "행복한개발자")
        String authorNickname,

        @Schema(description = "작성일시")
        LocalDateTime createdAt

) {
    public static PostListResponse from(CommunityPost post) {
        return new PostListResponse(
                post.getId(),
                post.getTitle(),
                post.getUser().getNickname(), // User 엔티티에 getNickname()이 있다고 가정
                post.getCreatedAt()
        );
    }
}
