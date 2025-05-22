package com.ownervoting.repository;

import com.ownervoting.model.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Community, Long> {
} 