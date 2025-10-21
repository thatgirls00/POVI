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

    @Transactional(readOnly = true)
    public Set<Long> getFollowingIds(Long viewerId) {
        return followRepository.findFollowingIds(viewerId);
    }

    @Transactional(readOnly = true)
    public Set<Long> getMutualFriendIds(Long viewerId) {
        return followRepository.findMutualFriendIds(viewerId);
    }




}
