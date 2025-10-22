package org.example.povi.domain.community.dto.response;


import lombok.Builder;



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
