package com.ownervoting.repository;

import com.ownervoting.model.entity.ReviewLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewLogRepository extends JpaRepository<ReviewLog, Long> {
} 