package org.example.povi.domain.community.repository;

import org.example.povi.domain.community.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<CommunityPost, Long> {


}
