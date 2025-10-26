package org.example.povi.domain.diary.like.repository;

import org.example.povi.domain.diary.like.entity.DiaryPostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DiaryPostLikeRepository extends JpaRepository<DiaryPostLike, Long> {

    // 특정 게시글에 대해 사용자가 좋아요를 눌렀는지 여부 확인
    boolean existsByPostIdAndUserId(Long postId, Long userId);

    // 특정 게시글의 전체 좋아요 개수
    long countByPostId(Long postId);

    // 특정 게시글 + 사용자 조합으로 좋아요 엔티티 조회 (토글용)
    Optional<DiaryPostLike> findByPostIdAndUserId(Long postId, Long userId);

    // 특정 게시글 + 사용자 조합으로 좋아요 삭제
    void deleteByPostIdAndUserId(Long postId, Long userId);

    // 여러 게시글에 대한 좋아요 수를 한 번에 집계
    @Query("select l.post.id, count(l) from DiaryPostLike l where l.post.id in :postIds group by l.post.id")
    List<Object[]> countByPostIds(@Param("postIds") List<Long> postIds);

    // 특정 사용자(userId)가 좋아요한 게시글 ID 목록 조회
    @Query("select l.post.id from DiaryPostLike l where l.user.id = :userId and l.post.id in :postIds")
    List<Long> findPostIdsLikedByUser(@Param("postIds") List<Long> postIds, @Param("userId") Long userId);
}