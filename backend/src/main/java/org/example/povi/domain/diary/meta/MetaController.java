package org.example.povi.domain.diary.meta;

import io.swagger.v3.oas.annotations.Operation;
import org.example.povi.domain.diary.enums.MoodEmoji;
import org.example.povi.domain.diary.enums.dto.MoodEmojiOptionRes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/meta")
public class MetaController {

    @GetMapping("/moods")
    @Operation(summary = "감정 이모지 옵션 목록 조회")
    public List<MoodEmojiOptionRes> getMoodOptions() {
        return Arrays.stream(MoodEmoji.values())
                .map(m -> new MoodEmojiOptionRes(m.name(), m.label()))
                .toList();
    }
}