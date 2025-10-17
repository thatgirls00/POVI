package org.example.povi.domain.diary.entry.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.povi.domain.diary.entry.dto.request.DiaryCreateReq;
import org.example.povi.domain.diary.entry.dto.request.DiaryUpdateReq;
import org.example.povi.domain.diary.entry.dto.response.DiaryCreateRes;
import org.example.povi.domain.diary.entry.dto.response.DiaryUpdateRes;
import org.example.povi.domain.diary.entry.service.DiaryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/diaries")
public class DiaryController {

    private final DiaryService diaryService;

    @PostMapping
    @Operation(summary = "다이어리 생성")
    public ResponseEntity<DiaryCreateRes> createDiary(@RequestBody @Valid DiaryCreateReq req) {
        DiaryCreateRes response = diaryService.create(req);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{diaryId}")
    @Operation(summary = "다이어리 부분수정")
    public ResponseEntity<DiaryUpdateRes> updateDiary(
            @PathVariable("diaryId") Long diaryId,
            @RequestBody @Valid DiaryUpdateReq req
    ) {
        DiaryUpdateRes response = diaryService.update(diaryId, req);
        return ResponseEntity.ok(response);
    }
}
