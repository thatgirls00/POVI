package org.example.povi.domain.community.dto.response;


public record PostDeleteResponse (
    Long postId,
    String message

) {

    public PostDeleteResponse {
    }
}