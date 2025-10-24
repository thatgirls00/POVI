package org.example.povi.domain.community.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.example.povi.domain.community.entity.CommunityEmoticon;
import org.example.povi.domain.community.entity.CommunityImage;
import org.example.povi.domain.community.entity.CommunityPost;


@Schema(description = "게시글 상세 조회 응답 DTO")
public record PostDetailResponse(
        @Schema(description = "게시글 ID", example = "101")
        Long postId,

        @Schema(description = "게시글 제목", example = "오늘 날씨가 정말 좋네요!")
        String title,

        @Schema(description = "게시글 본문", example = "집에만 있기 아까운 날씨입니다. 다들 즐거운 하루 보내세요.")
        String content,

        @Schema(description = "감정 이모티콘", example = "HAPPY")
        CommunityEmoticon emoticon,

        @Schema(description = "작성자 닉네임", example = "행복한개발자")
        String authorNickname,

        @Schema(description = "작성일시")
        LocalDateTime createdAt,

        @Schema(description = "댓글 목록")
        List<CommentResponse> comments,

        @Schema(description = "첨부된 사진 URL 목록")
        List<String> photoUrls

) {

    public static PostDetailResponse from(CommunityPost post) {
        List<String> urls = post.getImages().stream()
                .map(CommunityImage::getImageUrl) // Photo 엔티티에 getFileUrl()이 있다고 가정
                .collect(Collectors.toList());

        return new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getEmoticon(),
                post.getUser().getNickname(),
                post.getCreatedAt(),
                post.getComments().stream()
                        .map(CommentResponse::from)
                        .collect(Collectors.toList()),
                urls
        );
    }
}
