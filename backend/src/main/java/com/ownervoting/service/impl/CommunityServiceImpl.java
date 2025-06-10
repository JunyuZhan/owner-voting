package com.ownervoting.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ownervoting.exception.BusinessException;
import com.ownervoting.exception.NotFoundException;
import com.ownervoting.model.dto.CommunityApplicationDTO;
import com.ownervoting.model.dto.CommunityApplicationReviewDTO;
import com.ownervoting.model.entity.*;
import com.ownervoting.model.vo.CommunityVO;
import com.ownervoting.repository.CommunityRepository;
import com.ownervoting.repository.OwnerCommunityRelationRepository;
import com.ownervoting.repository.OwnerRepository;
import com.ownervoting.repository.HouseRepository;
import com.ownervoting.service.CommunityService;
import com.ownervoting.service.SystemLogService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CommunityServiceImpl implements CommunityService {

    @Autowired
    private CommunityRepository communityRepository;
    
    @Autowired
    private OwnerCommunityRelationRepository relationRepository;
    
    @Autowired
    private OwnerRepository ownerRepository;
    
    @Autowired
    private HouseRepository houseRepository;
    
    @Autowired
    private SystemLogService systemLogService;

    @Override
    @Transactional
    public Community addCommunity(Community community) {
        community.setCreatedAt(LocalDateTime.now());
        return communityRepository.save(community);
    }

    @Override
    @Transactional
    public void deleteCommunity(Long id) {
        communityRepository.deleteById(id);
    }

    @Override
    public Community findById(Long id) {
        return communityRepository.findById(id).orElse(null);
    }

    @Override
    public List<Community> findAll() {
        return communityRepository.findAll();
    }
    
    // 新增方法实现
    @Override
    public List<Community> getAllCommunities() {
        return communityRepository.findAll();
    }
    
    @Override
    public Community getCommunityById(Long id) {
        return communityRepository.findById(id).orElse(null);
    }
    
    @Override
    @Transactional
    public Community createCommunity(Community community) {
        community.setCreatedAt(LocalDateTime.now());
        return communityRepository.save(community);
    }
    
    @Override
    @Transactional
    public Community updateCommunity(Long id, Community community) {
        Optional<Community> existingOpt = communityRepository.findById(id);
        if (existingOpt.isPresent()) {
            Community existing = existingOpt.get();
            existing.setName(community.getName());
            existing.setAddress(community.getAddress());
            existing.setDescription(community.getDescription());
            return communityRepository.save(existing);
        }
        return null;
    }
    
    @Override
    @Transactional
    public boolean deleteCommunity(Long id, boolean returnBoolean) {
        if (communityRepository.existsById(id)) {
            communityRepository.deleteById(id);
            return true;
        }
        return false;
    }
    
    @Override
    @Transactional
    public Community save(Community community) {
        return communityRepository.save(community);
    }

    @Override
    @Transactional
    public OwnerCommunityRelation applyToCommunity(CommunityApplicationDTO dto) {
        log.info("业主{}申请加入小区{}", dto.getOwnerId(), dto.getCommunityId());
        
        // 1. 验证业主存在
        Owner owner = ownerRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> NotFoundException.of("业主", dto.getOwnerId()));
        
        // 2. 验证小区存在
        Community community = communityRepository.findById(dto.getCommunityId())
                .orElseThrow(() -> NotFoundException.of("小区", dto.getCommunityId()));
        
        // 3. 检查是否已有申请记录
        Optional<OwnerCommunityRelation> existingRelation = 
                relationRepository.findByOwnerIdAndCommunityId(dto.getOwnerId(), dto.getCommunityId());
        
        if (existingRelation.isPresent()) {
            OwnerCommunityRelation relation = existingRelation.get();
            switch (relation.getStatus()) {
                case APPROVED:
                    throw new BusinessException("您已是该小区的认证业主");
                case PENDING:
                    throw new BusinessException("您的申请正在审核中，请耐心等待");
                case REJECTED:
                    // 可以重新申请，更新现有记录
                    relation.setStatus(OwnerCommunityRelation.ApplicationStatus.PENDING);
                    relation.setApplicationReason(dto.getApplicationReason());
                    relation.setAppliedAt(LocalDateTime.now());
                    relation.setReviewedAt(null);
                    relation.setReviewerName(null);
                    relation.setReviewRemark(null);
                    return relationRepository.save(relation);
            }
        }
        
        // 4. 创建新的申请记录
        OwnerCommunityRelation relation = new OwnerCommunityRelation();
        relation.setOwner(owner);
        relation.setCommunity(community);
        relation.setApplicationReason(dto.getApplicationReason());
        relation.setStatus(OwnerCommunityRelation.ApplicationStatus.PENDING);
        
        OwnerCommunityRelation saved = relationRepository.save(relation);
        
        // 5. 记录系统日志
        SystemLog sysLog = new SystemLog();
        sysLog.setUserId(owner.getId());
        sysLog.setUserType(SystemLog.UserType.OWNER);
        sysLog.setOperation("申请加入小区");
        sysLog.setDetail(String.format("业主%s申请加入小区%s", owner.getName(), community.getName()));
        systemLogService.addLog(sysLog);
        
        return saved;
    }

    @Override
    @Transactional
    public void reviewApplication(CommunityApplicationReviewDTO dto) {
        log.info("审核小区申请{}，状态：{}", dto.getApplicationId(), dto.getStatus());
        
        OwnerCommunityRelation relation = relationRepository.findById(dto.getApplicationId())
                .orElseThrow(() -> NotFoundException.of("申请记录", dto.getApplicationId()));
        
        if (relation.getStatus() != OwnerCommunityRelation.ApplicationStatus.PENDING) {
            throw new BusinessException("该申请已被处理，无法重复审核");
        }
        
        // 更新申请状态
        switch (dto.getStatus()) {
            case "APPROVED":
                relation.setStatus(OwnerCommunityRelation.ApplicationStatus.APPROVED);
                break;
            case "REJECTED":
                relation.setStatus(OwnerCommunityRelation.ApplicationStatus.REJECTED);
                break;
            default:
                throw new BusinessException("无效的审核状态：" + dto.getStatus());
        }
        
        relation.setReviewedAt(LocalDateTime.now());
        relation.setReviewerName(dto.getReviewerName());
        relation.setReviewRemark(dto.getReviewRemark());
        
        relationRepository.save(relation);
        
        // 记录系统日志
        SystemLog sysLog = new SystemLog();
        sysLog.setUserId(relation.getOwner().getId());
        sysLog.setUserType(SystemLog.UserType.ADMIN);
        sysLog.setOperation("小区申请审核");
        sysLog.setDetail(String.format("审核业主%s加入小区%s的申请，结果：%s", 
                relation.getOwner().getName(), relation.getCommunity().getName(), dto.getStatus()));
        systemLogService.addLog(sysLog);
    }

    @Override
    public List<CommunityVO> findCommunitiesByOwner(Long ownerId) {
        // 获取所有小区
        List<Community> allCommunities = communityRepository.findAll();
        
        return allCommunities.stream().map(community -> {
            CommunityVO vo = new CommunityVO();
            vo.setId(community.getId());
            vo.setName(community.getName());
            vo.setAddress(community.getAddress());
            vo.setDescription(community.getDescription());
            vo.setCreatedAt(community.getCreatedAt());
            
            // 查找业主在该小区的关联状态
            Optional<OwnerCommunityRelation> relation = 
                    relationRepository.findByOwnerIdAndCommunityId(ownerId, community.getId());
            
            if (relation.isPresent()) {
                OwnerCommunityRelation r = relation.get();
                vo.setOwnerStatus(r.getStatus().name());
                vo.setAppliedAt(r.getAppliedAt());
                vo.setReviewedAt(r.getReviewedAt());
                vo.setReviewRemark(r.getReviewRemark());
            } else {
                vo.setOwnerStatus("NOT_APPLIED");
            }
            
            // 设置小区统计信息
            vo.setTotalOwners(relationRepository.countApprovedOwnersByCommunity(community.getId()));
            vo.setTotalHouses((long) houseRepository.findByCommunityIdAndVerificationStatus(
                    community.getId(), House.VerificationStatus.APPROVED).size());
            
            return vo;
        }).collect(Collectors.toList());
    }

    @Override
    public List<OwnerCommunityRelation> findPendingApplications(Long communityId) {
        return relationRepository.findPendingApplicationsByCommunity(communityId);
    }

    @Override
    public OwnerCommunityRelation.ApplicationStatus checkOwnerStatus(Long ownerId, Long communityId) {
        Optional<OwnerCommunityRelation> relation = 
                relationRepository.findByOwnerIdAndCommunityId(ownerId, communityId);
        
        return relation.map(OwnerCommunityRelation::getStatus).orElse(null);
    }

    @Override
    public Long countApprovedOwners(Long communityId) {
        return relationRepository.countApprovedOwnersByCommunity(communityId);
    }
} 