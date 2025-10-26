package org.example.povi.domain.diary.post.repository;

import org.example.povi.domain.diary.enums.Visibility;
import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.example.povi.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Repository
public interface DiaryPostRepository extends JpaRepository<DiaryPost, Long> {
    void deleteAllByUser(User user);

    // ======================================================================
    // "나의 다이어리" 조회 (월별 페이징 / 주간 비페이징)
    // ======================================================================
    Page<DiaryPost> findByUserIdAndCreatedAtBetween(
            Long userId,
            LocalDateTime startAt,
            LocalDateTime endAt,
            Pageable pageable
    );

    List<DiaryPost> findByUserIdAndCreatedAtBetween(
            Long userId,
            LocalDateTime startAt,
            LocalDateTime endAt
    );

    // ======================================================================
    // 친구 피드 (비페이징)
    // ======================================================================
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

    // ======================================================================
    // 친구 피드 (페이징)
    // ======================================================================
    @Query("""
    select p
    from DiaryPost p
    where (
            (:hasMutual = true and p.user.id in :mutualIds and p.visibility in :friendVisible)
         or (:hasOneWay = true and p.user.id in :oneWayIds and p.visibility = :publicVis)
    )
    order by p.createdAt desc
    """)
    Page<DiaryPost> findFriendFeedPaged(
            @Param("mutualIds") Collection<Long> mutualIds,
            @Param("friendVisible") Collection<Visibility> friendVisible,
            @Param("oneWayIds") Collection<Long> oneWayIds,
            @Param("publicVis") Visibility publicVis,
            @Param("hasMutual") boolean hasMutual,
            @Param("hasOneWay") boolean hasOneWay,
            Pageable pageable
    );

    // ======================================================================
    // Explore 피드 (기간 + 가시성 규칙)
    // ======================================================================
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
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );

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
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt
    );

    // ======================================================================
    // 집계/통계
    // ======================================================================
    @Query("""
            select c.post.id as postId, count(c) as cnt
            from DiaryComment c
            where c.post.id in :postIds
            group by c.post.id
            """)
    List<Object[]> countCommentsInPostIds(@Param("postIds") List<Long> postIds);

    long countByUserId(Long userId);
}