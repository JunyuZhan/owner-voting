package com.ownervoting.service;

import com.ownervoting.model.entity.ReviewLog;
import java.util.List;

public interface ReviewLogService {
    ReviewLog addReviewLog(ReviewLog reviewLog);
    void deleteReviewLog(Long id);
    ReviewLog findById(Long id);
    List<ReviewLog> findByTargetTypeAndTargetId(String targetType, Long targetId);
} 