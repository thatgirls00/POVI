package org.example.povi.domain.diary.post.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.example.povi.domain.diary.enums.MoodEmoji;
import org.example.povi.domain.diary.enums.Visibility;

import java.util.List;



public record DiaryPostCreateReq(
        @NotBlank @Size(min = 1, max = 50)
        String title,
        @NotBlank @Size(min = 1, max = 3000)
        String content,
        @NotNull
        MoodEmoji moodEmoji,
        @NotNull
        Visibility visibility,
        @Size(max = 3)
        List<String> imageUrls
) {}