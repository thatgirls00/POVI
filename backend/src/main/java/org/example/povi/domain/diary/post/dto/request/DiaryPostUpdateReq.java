package org.example.povi.domain.diary.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.example.povi.domain.diary.enums.MoodEmoji;
import org.example.povi.domain.diary.enums.Visibility;

import java.util.List;


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
