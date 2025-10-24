package org.example.povi.domain.diary.post.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.povi.domain.diary.enums.MoodEmoji;
import org.example.povi.domain.diary.enums.Visibility;

import java.util.List;

@Schema(description = "다이어리 게시글 수정 요청 DTO")
public record DiaryPostUpdateReq(
        @Size(min = 2, max = 50)
        String title,
        @Size(min = 10, max = 3000)
        String content,
        MoodEmoji moodEmoji,
        Visibility visibility,
        @Size(max = 3) List<@NotBlank @Size(max = 2048)
                String> imageUrls

) {
}
