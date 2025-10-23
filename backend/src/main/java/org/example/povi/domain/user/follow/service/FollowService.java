package org.example.povi.domain.user.follow.service;

import lombok.RequiredArgsConstructor;
import org.example.povi.domain.user.follow.repository.FollowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class FollowService {

    private final FollowRepository followRepository;

    /**
     * 내가 팔로우 중인 사용자 ID 목록 조회
     */
    @Transactional(readOnly = true)
    public Set<Long> getFollowingUserIds(Long userId) {
        return followRepository.findFollowingIds(userId);
    }

    /**
     * 맞팔(서로 팔로우 중인) 사용자 ID 목록 조회
     */
    @Transactional(readOnly = true)
    public Set<Long> getMutualUserIds(Long userId) {
        return followRepository.findMutualFriendIds(userId);
    }

    /**
     * 두 사용자가 맞팔인지 여부
     */
    @Transactional(readOnly = true)
    public boolean isMutualFollow(Long userId1, Long userId2) {
        if (userId1 == null || userId2 == null) return false;
        if (userId1.equals(userId2)) return true;
        return followRepository.findMutualFriendIds(userId1).contains(userId2);
    }
}
