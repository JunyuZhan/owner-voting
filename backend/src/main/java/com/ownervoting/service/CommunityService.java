package com.ownervoting.service;

import java.util.List;

import com.ownervoting.model.entity.Community;
import com.ownervoting.model.entity.OwnerCommunityRelation;
import com.ownervoting.model.dto.CommunityApplicationDTO;
import com.ownervoting.model.dto.CommunityApplicationReviewDTO;
import com.ownervoting.model.vo.CommunityVO;

public interface CommunityService {
    Community addCommunity(Community community);
    void deleteCommunity(Long id);
    Community findById(Long id);
    List<Community> findAll();
    List<Community> getAllCommunities();
    Community getCommunityById(Long id);
    Community createCommunity(Community community);
    Community updateCommunity(Long id, Community community);
    boolean deleteCommunity(Long id, boolean returnBoolean);
    Community save(Community community);
    
    // 业主-小区关联管理
    
    /**
     * 业主申请加入小区
     */
    OwnerCommunityRelation applyToCommunity(CommunityApplicationDTO dto);
    
    /**
     * 审核业主加入小区申请
     */
    void reviewApplication(CommunityApplicationReviewDTO dto);
    
    /**
     * 查找业主已加入的小区列表
     */
    List<CommunityVO> findCommunitiesByOwner(Long ownerId);
    
    /**
     * 查找小区的待审核申请
     */
    List<OwnerCommunityRelation> findPendingApplications(Long communityId);
    
    /**
     * 检查业主在小区的权限状态
     */
    OwnerCommunityRelation.ApplicationStatus checkOwnerStatus(Long ownerId, Long communityId);
    
    /**
     * 获取小区业主统计
     */
    Long countApprovedOwners(Long communityId);
} 