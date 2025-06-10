package com.ownervoting.controller;

import com.ownervoting.model.dto.CommunityApplicationDTO;
import com.ownervoting.model.dto.CommunityApplicationReviewDTO;
import com.ownervoting.model.entity.Community;
import com.ownervoting.model.entity.OwnerCommunityRelation;
import com.ownervoting.model.vo.ApiResponse;
import com.ownervoting.model.vo.CommunityVO;
import com.ownervoting.service.CommunityService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/community")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    /**
     * 获取所有小区列表（公开接口）
     */
    @GetMapping("/public")
    public ApiResponse<List<Community>> getAllCommunities() {
        List<Community> communities = communityService.findAll();
        return ApiResponse.success(communities);
    }

    /**
     * 获取业主的小区列表（包含申请状态）
     */
    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasRole('OWNER') or hasAnyRole('SYSTEM_ADMIN', 'COMMUNITY_ADMIN')")
    public ApiResponse<List<CommunityVO>> getOwnerCommunities(@PathVariable Long ownerId) {
        List<CommunityVO> communities = communityService.findCommunitiesByOwner(ownerId);
        return ApiResponse.success(communities);
    }

    /**
     * 业主申请加入小区
     */
    @PostMapping("/apply")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<OwnerCommunityRelation> applyToCommunity(@Valid @RequestBody CommunityApplicationDTO dto) {
        log.info("业主{}申请加入小区{}", dto.getOwnerId(), dto.getCommunityId());
        
        OwnerCommunityRelation relation = communityService.applyToCommunity(dto);
        return ApiResponse.success(relation);
    }

    /**
     * 检查业主在小区的状态
     */
    @GetMapping("/status")
    @PreAuthorize("hasRole('OWNER') or hasAnyRole('SYSTEM_ADMIN', 'COMMUNITY_ADMIN')")
    public ApiResponse<Map<String, Object>> checkOwnerStatus(
            @RequestParam Long ownerId, 
            @RequestParam Long communityId) {
        
        OwnerCommunityRelation.ApplicationStatus status = 
                communityService.checkOwnerStatus(ownerId, communityId);
        
        return ApiResponse.success(Map.of(
                "ownerId", ownerId,
                "communityId", communityId,
                "status", status != null ? status.name() : "NOT_APPLIED",
                "canVote", status == OwnerCommunityRelation.ApplicationStatus.APPROVED
        ));
    }

    // ==================== 管理员接口 ====================

    /**
     * 获取小区待审核申请列表
     */
    @GetMapping("/{communityId}/pending-applications")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'COMMUNITY_ADMIN')")
    public ApiResponse<List<OwnerCommunityRelation>> getPendingApplications(@PathVariable Long communityId) {
        List<OwnerCommunityRelation> applications = communityService.findPendingApplications(communityId);
        return ApiResponse.success(applications);
    }

    /**
     * 审核业主加入小区申请
     */
    @PostMapping("/admin/review-application")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'COMMUNITY_ADMIN')")
    public ApiResponse<String> reviewApplication(@Valid @RequestBody CommunityApplicationReviewDTO dto) {
        log.info("管理员{}审核小区申请{}，结果：{}", dto.getReviewerName(), dto.getApplicationId(), dto.getStatus());
        
        communityService.reviewApplication(dto);
        return ApiResponse.success("审核完成");
    }

    /**
     * 获取小区基本信息和统计
     */
    @GetMapping("/{id}/info")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'COMMUNITY_ADMIN')")
    public ApiResponse<Map<String, Object>> getCommunityInfo(@PathVariable Long id) {
        Community community = communityService.findById(id);
        if (community == null) {
            return ApiResponse.error(404, "小区不存在");
        }
        
        Long approvedOwners = communityService.countApprovedOwners(id);
        
        return ApiResponse.success(Map.of(
                "community", community,
                "approvedOwners", approvedOwners,
                "description", "小区基本信息和统计数据"
        ));
    }

    /**
     * 创建新小区
     */
    @PostMapping("/admin/create")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ApiResponse<Community> createCommunity(@Valid @RequestBody Community community) {
        log.info("创建新小区：{}", community.getName());
        
        Community saved = communityService.save(community);
        return ApiResponse.success(saved);
    }

    /**
     * 更新小区信息
     */
    @PutMapping("/admin/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'COMMUNITY_ADMIN')")
    public ApiResponse<Community> updateCommunity(@PathVariable Long id, @Valid @RequestBody Community community) {
        log.info("更新小区{}信息", id);
        
        Community existing = communityService.findById(id);
        if (existing == null) {
            return ApiResponse.error(404, "小区不存在");
        }
        
        community.setId(id);
        Community saved = communityService.save(community);
        return ApiResponse.success(saved);
    }
} 