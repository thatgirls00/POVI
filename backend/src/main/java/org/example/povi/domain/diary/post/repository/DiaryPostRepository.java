package org.example.povi.domain.diary.post.repository;

import org.example.povi.domain.diary.post.entity.DiaryPost;
import org.example.povi.domain.diary.enums.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface DiaryPostRepository extends JpaRepository<DiaryPost, Long> {
    List<DiaryPost> findByUserIdOrderByCreatedAtDesc(Long userId);

    //여러 작성자 + 가시성 (전체 기간, 최신순)
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
}

