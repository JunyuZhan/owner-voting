package com.ownervoting.controller;

import com.ownervoting.model.entity.ReviewLog;
import com.ownervoting.service.ReviewLogService;
import com.ownervoting.model.vo.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/review-log")
public class ReviewLogController {

    @Autowired
    private ReviewLogService reviewLogService;

    @PostMapping("/add")
    public ApiResponse<ReviewLog> addReviewLog(@RequestBody ReviewLog reviewLog) {
        return ApiResponse.success(reviewLogService.addReviewLog(reviewLog));
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteReviewLog(@PathVariable Long id) {
        reviewLogService.deleteReviewLog(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}")
    public ApiResponse<ReviewLog> getById(@PathVariable Long id) {
        return ApiResponse.success(reviewLogService.findById(id));
    }

    @GetMapping("/by-target")
    public ApiResponse<List<ReviewLog>> getByTarget(@RequestParam String targetType, @RequestParam Long targetId) {
        return ApiResponse.success(reviewLogService.findByTargetTypeAndTargetId(targetType, targetId));
    }
} 