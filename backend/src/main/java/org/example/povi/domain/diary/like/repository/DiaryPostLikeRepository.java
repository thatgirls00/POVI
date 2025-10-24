package org.example.povi.domain.diary.like.repository;

import org.example.povi.domain.diary.like.entity.DiaryPostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DiaryPostLikeRepository extends JpaRepository<DiaryPostLike, Long> {

    boolean existsByPostIdAndUserId(Long postId, Long userId);

    long countByPostId(Long postId);

    Optional<DiaryPostLike> findByPostIdAndUserId(Long postId, Long userId);

    void deleteByPostIdAndUserId(Long postId, Long userId);

    // 여러 게시글 좋아요 수 한 번에
    @Query("select l.post.id, count(l) from DiaryPostLike l where l.post.id in :postIds group by l.post.id")
    List<Object[]> countByPostIds(@Param("postIds") List<Long> postIds);

    // 현재 유저가 좋아요한 게시글 id 목록
    @Query("select l.post.id from DiaryPostLike l where l.user.id = :userId and l.post.id in :postIds")
    List<Long> findPostIdsLikedByUser(@Param("postIds") List<Long> postIds, @Param("userId") Long userId);
}