package org.example.povi.transcription.service;

import lombok.RequiredArgsConstructor;
import org.example.povi.domain.Quote;
import org.example.povi.domain.Transcription;
import org.example.povi.domain.User;
import org.example.povi.global.exception.AuthorizationException;
import org.example.povi.global.exception.DuplicateTranscriptionException;
import org.example.povi.global.exception.ResourceNotFoundException;
import org.example.povi.quote.repository.QuoteRepository;
import org.example.povi.transcription.dto.TranscriptionReq;
import org.example.povi.transcription.repository.TranscriptionRepository;
import org.example.povi.user.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class TranscriptionService {

    private final UserRepository userRepository;
    private final QuoteRepository quoteRepository;
    private final TranscriptionRepository transcriptionRepository;

    @Transactional
    public void createTranscription(Long userId, Long quoteId, TranscriptionReq transcriptionReq) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다: ID " + userId));
        Quote quote = quoteRepository.findById(quoteId)
                .orElseThrow(() -> new ResourceNotFoundException("명언을 찾을 수 없습니다: ID " + quoteId));

        LocalDate quoteDate = LocalDate.from(quote.getCreatedAt());

        Transcription transcription = new Transcription(transcriptionReq.getContent(), quote, user);

        try {
            transcriptionRepository.save(transcription);
        } catch (DataIntegrityViolationException e) {
            // DB의 유니크 제약 조건 위반 시 이 예외가 발생
            throw new DuplicateTranscriptionException("이미 필사한 명언입니다.");
        }
    }

    @Transactional
    public void deleteTranscription(Long userId, Long transcriptionId) {
        Transcription transcription = transcriptionRepository.findById(transcriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 필사 기록을 찾을 수 없습니다."));

        if(!transcription.getUser().getId().equals(userId)) {
            throw new AuthorizationException("삭제 권한이 없습니다.");
        }

        transcriptionRepository.delete(transcription);
    }
}
