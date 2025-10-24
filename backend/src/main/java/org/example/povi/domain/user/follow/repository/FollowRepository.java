package org.example.povi.domain.user.follow.repository;

import org.example.povi.domain.user.entity.User;
import org.example.povi.domain.user.follow.entity.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;
@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    void deleteAllByFollowerOrFollowing(User follower, User following);

    // 내가 팔로우 중인 사용자들
    @Query("""
            select f.following.id
            from Follow f
            where f.follower.id = :viewerId
            """)
    Set<Long> findFollowingIds(@Param("viewerId") Long viewerId);

    // 맞팔(친구) 사용자들
    @Query("""
            select f1.following.id
            from Follow f1
            where f1.follower.id = :viewerId
              and exists (
                  select 1 from Follow f2
                  where f2.follower.id = f1.following.id
                    and f2.following.id = :viewerId
              )
            """)
    Set<Long> findMutualFriendIds(@Param("viewerId") Long viewerId);
}
