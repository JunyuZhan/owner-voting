package com.ownervoting.service;

import com.ownervoting.model.entity.House;
import com.ownervoting.model.entity.House.VerificationStatus;
import com.ownervoting.model.dto.HouseRegistrationDTO;
import com.ownervoting.model.dto.HouseVerificationDTO;
import com.ownervoting.model.vo.HouseConflictVO;

import java.util.List;
import java.util.Optional;

public interface HouseService {
    
    /**
     * 基础房屋管理
     */
    House addHouse(House house);
    void deleteHouse(Long id);
    House updateHouse(House house);
    
    /**
     * 注册新房屋（带重复检测）
     */
    House registerHouse(HouseRegistrationDTO dto);
    
    /**
     * 检测房屋位置冲突
     */
    HouseConflictVO checkHouseConflict(Long communityId, String building, String unit, String room);
    
    /**
     * 房屋认证审核
     */
    void verifyHouse(HouseVerificationDTO dto);
    
    /**
     * 处理房屋争议
     */
    void handleHouseDispute(Long houseId, String action, String reason, String adminName);
    
    /**
     * 查找业主的房屋列表
     */
    List<House> findByOwnerId(Long ownerId);
    
    /**
     * 根据ID查找房屋
     */
    House findById(Long id);
    
    /**
     * 查找待审核的房屋
     */
    List<House> findPendingHouses();
    
    /**
     * 查找存在争议的房屋
     */
    List<House> findDisputedHouses();
    
    /**
     * 根据认证状态查找房屋
     */
    List<House> findByVerificationStatus(VerificationStatus status);
    
    /**
     * 根据小区ID和认证状态查找房屋
     */
    List<House> findByCommunityIdAndVerificationStatus(Long communityId, VerificationStatus status);
    
    /**
     * 检查业主是否可以参与投票（至少有一套已认证房屋）
     */
    boolean canOwnerVote(Long ownerId);
    
    /**
     * 获取业主的投票权重（基于已认证房屋数量）
     */
    int getVotingWeight(Long ownerId);
} 