package org.example.povi.domain.community.repository;

import java.util.Optional;
import org.example.povi.domain.community.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByUserIdAndPostId(Long userId, Long postId);
}
