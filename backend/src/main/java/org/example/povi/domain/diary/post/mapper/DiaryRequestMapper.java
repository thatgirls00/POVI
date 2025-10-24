package org.example.povi.domain.diary.post.mapper;

import org.example.povi.domain.diary.post.dto.request.DiaryPostCreateReq;
import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.example.povi.domain.user.entity.User;

public final class DiaryRequestMapper {

    private DiaryRequestMapper() {
    }

    /**
     * DiaryCreateReq → DiaryEntry 엔티티 변환
     */
    public static DiaryPost fromCreateRequest(DiaryPostCreateReq req, User author) {
        return DiaryPost.create(author, req.title(), req.content(), req.moodEmoji(), req.visibility(), req.imageUrls());
    }
}