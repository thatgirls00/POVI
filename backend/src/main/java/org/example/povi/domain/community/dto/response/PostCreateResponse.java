package org.example.povi.domain.community.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;


@Schema(description = "게시글 작성 완료 응답 DTO")
public record PostCreateResponse (
    Long postId,
    String message

)   {
    @Builder
    public PostCreateResponse(Long postId, String message) {
        this.postId =postId;
        this.message =message;
    }
}
