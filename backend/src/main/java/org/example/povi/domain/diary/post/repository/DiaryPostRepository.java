package org.example.povi.domain.diary.post.repository;

import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.example.povi.domain.diary.enums.Visibility;
import org.example.povi.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface DiaryPostRepository extends JpaRepository<DiaryPost, Long> {
    void deleteAllByUser(User user);

    /**
     * 특정 사용자의 다이어리 전체 조회 (최신순)
     */
    List<DiaryPost> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * 여러 작성자의 다이어리를 가시성 조건으로 조회 (최신순)
     */
    @Query("""
            select p
            from DiaryPost p
            where p.user.id in :authorIds
              and p.visibility in :visibilities
            order by p.createdAt desc
            """)
    List<DiaryPost> findByAuthorsAndVisibilityOrderByCreatedAtDesc(
            @Param("authorIds") Collection<Long> authorIds,
            @Param("visibilities") Collection<Visibility> visibilities
    );

    /**
     * Explore 피드 조회 - 맞팔 사용자(FRIEND+PUBLIC), 그 외 사용자(PUBLIC)
     */
    @Query("""
            select p
            from DiaryPost p
            where p.user.id <> :viewerId
              and p.createdAt >= :startAt
              and p.createdAt < :endAt
              and (
                    (p.user.id in :mutualIds and p.visibility in :friendVisible)
                 or (p.user.id not in :mutualIds and p.visibility = :publicVis)
              )
            order by p.createdAt desc
            """)
    List<DiaryPost> findExploreFeedWithMutualsInPeriod(
            @Param("viewerId") Long viewerId,
            @Param("mutualIds") Collection<Long> mutualIds,
            @Param("friendVisible") Collection<Visibility> friendVisible,
            @Param("publicVis") Visibility publicVis,
            @Param("startAt") java.time.LocalDateTime startAt,
            @Param("endAt") java.time.LocalDateTime endAt
    );

    /**
     * Explore 피드 조회 - 맞팔 사용자가 없는 경우 (PUBLIC만 조회)
     */
    @Query("""
            select p
            from DiaryPost p
            where p.user.id <> :viewerId
              and p.createdAt >= :startAt
              and p.createdAt < :endAt
              and p.visibility = :publicVis
            order by p.createdAt desc
            """)
    List<DiaryPost> findExploreFeedPublicOnlyInPeriod(
            @Param("viewerId") Long viewerId,
            @Param("publicVis") Visibility publicVis,
            @Param("startAt") java.time.LocalDateTime startAt,
            @Param("endAt") java.time.LocalDateTime endAt
    );

    /**
     * 여러 게시글에 대한 댓글 수 집계
     */
    @Query("""
              select c.post.id as postId, count(c) as cnt
              from DiaryComment c
              where c.post.id in :postIds
              group by c.post.id
            """)
    List<Object[]> countCommentsInPostIds(@Param("postIds") List<Long> postIds);


    /**
     * 특정 사용자의 전체 다이어리 개수
     */
    long countByUserId(Long userId);
}

