package com.ownervoting.controller;

import com.ownervoting.model.vo.ApiResponse;
import com.ownervoting.model.entity.CommunityAdminApplication;
import com.ownervoting.model.dto.CommunityAdminApplicationDTO;
import com.ownervoting.model.dto.CommunityAdminApplicationReviewDTO;
import com.ownervoting.service.CommunityAdminApplicationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/community-admin-application")
@Validated
public class CommunityAdminApplicationController {

    @Autowired
    private CommunityAdminApplicationService applicationService;

    /**
     * 提交小区管理员申请（公开接口）
     */
    @PostMapping("/submit")
    public ApiResponse<CommunityAdminApplication> submitApplication(@Valid @RequestBody CommunityAdminApplicationDTO dto) {
        log.info("收到小区管理员申请: 申请人={}, 小区={}", dto.getApplicantName(), dto.getCommunityName());
        
        CommunityAdminApplication application = applicationService.submitApplication(dto);
        return ApiResponse.success(application);
    }

    /**
     * 查询申请状态（公开接口）
     */
    @GetMapping("/status")
    public ApiResponse<List<CommunityAdminApplication>> getApplicationStatus(@RequestParam String phone) {
        List<CommunityAdminApplication> applications = applicationService.findByApplicantPhone(phone);
        return ApiResponse.success(applications);
    }

    // ==================== 系统管理员接口 ====================

    /**
     * 获取待审核申请列表
     */
    @GetMapping("/pending")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ApiResponse<List<CommunityAdminApplication>> getPendingApplications() {
        List<CommunityAdminApplication> applications = applicationService.findPendingApplications();
        return ApiResponse.success(applications);
    }

    /**
     * 根据状态获取申请列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ApiResponse<List<CommunityAdminApplication>> getApplications(
            @RequestParam(required = false) String status) {
        List<CommunityAdminApplication> applications = applicationService.findApplicationsByStatus(status);
        return ApiResponse.success(applications);
    }

    /**
     * 获取申请详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ApiResponse<CommunityAdminApplication> getApplicationDetail(@PathVariable Long id) {
        CommunityAdminApplication application = applicationService.findById(id);
        return ApiResponse.success(application);
    }

    /**
     * 审核申请
     */
    @PostMapping("/review")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ApiResponse<String> reviewApplication(@Valid @RequestBody CommunityAdminApplicationReviewDTO dto) {
        log.info("审核小区管理员申请: 申请ID={}, 状态={}", dto.getApplicationId(), dto.getStatus());
        
        applicationService.reviewApplication(dto);
        return ApiResponse.success("审核完成");
    }

    /**
     * 获取申请统计信息
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ApiResponse<Map<String, Long>> getStatistics() {
        Map<String, Long> statistics = applicationService.getApplicationStatistics();
        return ApiResponse.success(statistics);
    }
} 