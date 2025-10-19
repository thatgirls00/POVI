package org.example.povi.domain.diary.entry.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.povi.domain.diary.type.MoodEmoji;
import org.example.povi.domain.diary.type.Visibility;

import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DiaryCreateReq {

    @NotNull
    private Long userId;

    @NotBlank
    @Size(min = 2, max = 50)
    private String title;

    @NotBlank
    @Size(min = 10, max = 3000)
    private String content;

    @NotNull
    private MoodEmoji moodEmoji;

    private Visibility visibility;

    @Size(max = 3)
    private List<String> imageUrls;
}