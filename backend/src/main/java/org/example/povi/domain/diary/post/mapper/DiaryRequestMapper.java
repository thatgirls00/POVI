package org.example.povi.domain.diary.post.mapper;

import org.example.povi.domain.diary.post.dto.request.DiaryPostCreateReq;
import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.example.povi.domain.user.entity.User;

public final class DiaryRequestMapper {

    private DiaryRequestMapper() {}    // 생성 응답
    /**
     * DiaryCreateReq → DiaryEntry 엔티티 변환
     *
     * @param request  일기 생성 요청 DTO
     * @param author   작성자(User 엔티티)
     * @return DiaryEntry 엔티티
     */
    public static DiaryPost fromCreateRequest(DiaryPostCreateReq request, User author) {
        return DiaryPost.builder()
                .user(author)
                .title(request.title())
                .content(request.content())
                .moodEmoji(request.moodEmoji())
                .visibility(request.visibility())
                .build();
    }
}
