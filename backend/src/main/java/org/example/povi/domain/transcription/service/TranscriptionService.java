package org.example.povi.domain.transcription.service;

import lombok.RequiredArgsConstructor;
import org.example.povi.domain.quote.entity.Quote;
import org.example.povi.domain.transcription.dto.TranscriptionReq;
import org.example.povi.domain.transcription.entity.Transcription;
import org.example.povi.domain.user.entity.User;
import org.example.povi.global.exception.ex.AuthorizationException;
import org.example.povi.global.exception.ex.DuplicateTranscriptionException;
import org.example.povi.global.exception.ex.ResourceNotFoundException;
import org.example.povi.domain.quote.repository.QuoteRepository;
import org.example.povi.domain.transcription.dto.TranscriptionDetail;
import org.example.povi.domain.transcription.dto.TranscriptionListRes;
import org.example.povi.domain.transcription.dto.TranscriptionRes;
import org.example.povi.domain.transcription.repository.TranscriptionRepository;
import org.example.povi.domain.user.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TranscriptionService {

    private final UserRepository userRepository;
    private final QuoteRepository quoteRepository;
    private final TranscriptionRepository transcriptionRepository;

    // 필사 저장
    @Transactional
    public TranscriptionRes createTranscription(Long userId, Long quoteId, TranscriptionReq transcriptionReq) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: ID " + userId));
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new ResourceNotFoundException("명언을 찾을 수 없습니다: ID " + quoteId));

        Transcription transcription = new Transcription(transcriptionReq.getContent(), quote, user);

        try {
            Transcription savedTranscription = transcriptionRepository.save(transcription);
            return TranscriptionRes.fromEntity(savedTranscription);
        } catch (DataIntegrityViolationException e) {
            // DB의 유니크 제약 조건 위반 시 이 예외가 발생
            throw new DuplicateTranscriptionException("이미 필사한 명언입니다.");
        }
    }

    // 필사 삭제
    @Transactional
    public void deleteTranscription(Long userId, Long transcriptionId) {
        Transcription transcription = transcriptionRepository.findById(transcriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 필사 기록을 찾을 수 없습니다."));

        if(!transcription.getUser().getId().equals(userId)) {
            throw new AuthorizationException("삭제 권한이 없습니다.");
        }

        transcriptionRepository.delete(transcription);
    }

    // 필사기록 조회
    @Transactional(readOnly = true)
    public TranscriptionListRes getMyTranscriptions(Long userId) {

        List<Transcription> transcriptions = transcriptionRepository.findAllByUserIdOrderByCreatedAtDesc(userId);

        List<TranscriptionDetail> dtoList = transcriptions.stream()
                .map(TranscriptionDetail::fromEntity)
                .toList();
        return new TranscriptionListRes(dtoList);
    }
}
