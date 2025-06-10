package com.ownervoting.service;

import com.ownervoting.model.entity.CommunityAdminApplication;
import com.ownervoting.model.dto.CommunityAdminApplicationDTO;
import com.ownervoting.model.dto.CommunityAdminApplicationReviewDTO;

import java.util.List;

public interface CommunityAdminApplicationService {
    
    /**
     * 提交小区管理员申请
     */
    CommunityAdminApplication submitApplication(CommunityAdminApplicationDTO dto);
    
    /**
     * 审核小区管理员申请
     */
    void reviewApplication(CommunityAdminApplicationReviewDTO dto);
    
    /**
     * 获取所有待审核申请
     */
    List<CommunityAdminApplication> findPendingApplications();
    
    /**
     * 获取所有申请（按状态筛选）
     */
    List<CommunityAdminApplication> findApplicationsByStatus(String status);
    
    /**
     * 根据ID获取申请详情
     */
    CommunityAdminApplication findById(Long id);
    
    /**
     * 根据申请人手机号查找申请历史
     */
    List<CommunityAdminApplication> findByApplicantPhone(String phone);
    
    /**
     * 获取申请统计信息
     */
    java.util.Map<String, Long> getApplicationStatistics();
} 