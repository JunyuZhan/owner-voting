package com.ownervoting.service.impl;

import com.ownervoting.model.entity.ReviewLog;
import com.ownervoting.repository.ReviewLogRepository;
import com.ownervoting.service.ReviewLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ReviewLogServiceImpl implements ReviewLogService {

    @Autowired
    private ReviewLogRepository reviewLogRepository;

    @Override
    @Transactional
    public ReviewLog addReviewLog(ReviewLog reviewLog) {
        return reviewLogRepository.save(reviewLog);
    }

    @Override
    @Transactional
    public void deleteReviewLog(Long id) {
        reviewLogRepository.deleteById(id);
    }

    @Override
    public ReviewLog findById(Long id) {
        return reviewLogRepository.findById(id).orElse(null);
    }

    @Override
    public List<ReviewLog> findByTargetTypeAndTargetId(String targetType, Long targetId) {
        return reviewLogRepository.findAll().stream()
                .filter(r -> r.getTargetType().equals(targetType) && r.getTargetId().equals(targetId))
                .toList();
    }
} 