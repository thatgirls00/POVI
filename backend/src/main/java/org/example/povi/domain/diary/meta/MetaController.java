package org.example.povi.domain.diary.meta;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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


    @Operation(summary = "감정 이모지 옵션 목록 조회", description = "일기 작성 시 사용할 수 있는 감정 이모지 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = MoodEmojiOptionRes.class))))
    })
    @GetMapping("/moods")
    public List<MoodEmojiOptionRes> getMoodOptions() {
        return Arrays.stream(MoodEmoji.values())
                .map(m -> new MoodEmojiOptionRes(m.name(), m.label()))
                .toList();
    }
}