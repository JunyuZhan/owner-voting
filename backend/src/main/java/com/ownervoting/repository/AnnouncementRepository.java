package com.ownervoting.repository;

import com.ownervoting.model.entity.Announcement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
    
    @Query("SELECT a FROM Announcement a WHERE a.community.id = :communityId")
    List<Announcement> findByCommunityId(@Param("communityId") Long communityId);
    
    @Override
    @Query("SELECT a FROM Announcement a WHERE a.id = :id")
    Optional<Announcement> findById(@Param("id") Long id);
}