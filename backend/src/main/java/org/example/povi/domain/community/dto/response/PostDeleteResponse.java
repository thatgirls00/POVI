package org.example.povi.domain.community.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public record PostDeleteResponse (
    Long postId,
    String message

) {

    public PostDeleteResponse {
    }
}