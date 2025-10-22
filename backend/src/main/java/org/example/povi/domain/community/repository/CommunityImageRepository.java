package org.example.povi.domain.community.repository;

import org.example.povi.domain.community.entity.CommunityImage;
import org.example.povi.domain.community.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityImageRepository extends JpaRepository<CommunityImage, Long> {

    void deleteAllByCommunityPost(CommunityPost communityPost);

}
