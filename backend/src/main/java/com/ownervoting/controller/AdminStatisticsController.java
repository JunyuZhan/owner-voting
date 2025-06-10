package com.ownervoting.controller;

import com.ownervoting.model.vo.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminStatisticsController {

    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','COMMUNITY_ADMIN','OPERATOR')")
    @GetMapping("/statistics")
    public ApiResponse<Map<String, Object>> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();
        
        // 模拟统计数据，后续可以从数据库获取真实数据
        statistics.put("totalUsers", 150);
        statistics.put("totalVotes", 25);
        statistics.put("activeVotes", 5);
        statistics.put("totalAnnouncements", 12);
        statistics.put("pendingReviews", 3);
        statistics.put("totalCommunities", 8);
        
        return ApiResponse.success(statistics);
    }
}