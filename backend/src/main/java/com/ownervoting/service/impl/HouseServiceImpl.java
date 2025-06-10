package com.ownervoting.service.impl;

import com.ownervoting.exception.BusinessException;
import com.ownervoting.exception.NotFoundException;
import com.ownervoting.model.dto.HouseRegistrationDTO;
import com.ownervoting.model.dto.HouseVerificationDTO;
import com.ownervoting.model.entity.*;
import com.ownervoting.model.vo.HouseConflictVO;
import com.ownervoting.repository.HouseRepository;
import com.ownervoting.repository.CommunityRepository;
import com.ownervoting.repository.OwnerRepository;
import com.ownervoting.service.HouseService;
import com.ownervoting.service.SystemLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class HouseServiceImpl implements HouseService {

    @Autowired
    private HouseRepository houseRepository;
    
    @Autowired
    private CommunityRepository communityRepository;
    
    @Autowired
    private OwnerRepository ownerRepository;
    
    @Autowired
    private SystemLogService systemLogService;

    // ==================== 基础CRUD方法 ====================

    @Override
    @Transactional
    public House addHouse(House house) {
        return houseRepository.save(house);
    }

    @Override
    @Transactional
    public void deleteHouse(Long id) {
        houseRepository.deleteById(id);
    }

    @Override
    @Transactional
    public House updateHouse(House house) {
        return houseRepository.save(house);
    }

    // ==================== 房屋认证相关方法 ====================

    @Override
    @Transactional
    public House registerHouse(HouseRegistrationDTO dto) {
        log.info("业主{}申请注册房屋：{}-{}-{}-{}", 
                dto.getOwnerId(), dto.getCommunityId(), dto.getBuilding(), dto.getUnit(), dto.getRoom());
        
        // 1. 验证业主存在
        Owner owner = ownerRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> NotFoundException.of("业主", dto.getOwnerId()));
        
        // 2. 验证小区存在
        Community community = communityRepository.findById(dto.getCommunityId())
                .orElseThrow(() -> NotFoundException.of("小区", dto.getCommunityId()));
        
        // 3. 检测房屋位置冲突
        HouseConflictVO conflict = checkHouseConflict(dto.getCommunityId(), 
                dto.getBuilding(), dto.getUnit(), dto.getRoom());
        
        if (conflict.getHasConflict()) {
            // 如果已有认证通过的房屋，直接拒绝
            if (conflict.getConflictType() == HouseConflictVO.ConflictType.ALREADY_APPROVED) {
                throw new BusinessException("该房屋已被其他业主认证，如有争议请联系管理员");
            }
            log.warn("房屋注册存在冲突：{}", conflict.getConflictMessage());
        }
        
        // 4. 检查房产证号是否重复
        Optional<House> existingByCert = houseRepository.findByCertificateNumber(dto.getCertificateNumber());
        if (existingByCert.isPresent()) {
            throw new BusinessException("该房产证号已被使用");
        }
        
        // 5. 创建房屋记录
        House house = new House();
        house.setOwner(owner);
        house.setCommunity(community);
        house.setBuilding(dto.getBuilding());
        house.setUnit(dto.getUnit());
        house.setRoom(dto.getRoom());
        house.setAddress(dto.getAddress());
        house.setArea(dto.getArea());
        house.setCertificateNumber(dto.getCertificateNumber());
        house.setIsPrimary(dto.getIsPrimary());
        house.setVerificationStatus(House.VerificationStatus.PENDING);
        house.setHasDispute(conflict.getHasConflict());
        house.setVerificationRemark(dto.getRemark());
        
        // 6. 如果存在冲突，标记相关房屋为争议状态
        if (conflict.getHasConflict()) {
            for (House conflictHouse : conflict.getConflictHouses()) {
                conflictHouse.setHasDispute(true);
                conflictHouse.setVerificationStatus(House.VerificationStatus.DISPUTED);
                houseRepository.save(conflictHouse);
            }
        }
        
        House savedHouse = houseRepository.save(house);
        
        // 7. 记录系统日志
        SystemLog sysLog = new SystemLog();
        sysLog.setUserId(owner.getId());
        sysLog.setUserType(SystemLog.UserType.OWNER);
        sysLog.setOperation("房屋注册");
        sysLog.setDetail(String.format("注册房屋：%s，房产证：%s，冲突状态：%s", 
                house.getUniqueKey(), dto.getCertificateNumber(), conflict.getHasConflict()));
        systemLogService.addLog(sysLog);
        
        return savedHouse;
    }

    @Override
    public HouseConflictVO checkHouseConflict(Long communityId, String building, String unit, String room) {
        // 1. 查找同位置的所有房屋
        List<House> sameLocationHouses = houseRepository.findByLocation(communityId, building, unit, room);
        
        if (sameLocationHouses.isEmpty()) {
            return HouseConflictVO.noConflict();
        }
        
        // 2. 检查是否有已认证的房屋
        Optional<House> approvedHouse = sameLocationHouses.stream()
                .filter(h -> h.getVerificationStatus() == House.VerificationStatus.APPROVED)
                .findFirst();
        
        if (approvedHouse.isPresent()) {
            return HouseConflictVO.conflict(
                    HouseConflictVO.ConflictType.ALREADY_APPROVED,
                    List.of(approvedHouse.get()),
                    "该位置已有认证通过的房屋，业主：" + approvedHouse.get().getOwner().getName()
            );
        }
        
        // 3. 检查是否有待审核的房屋
        List<House> pendingHouses = sameLocationHouses.stream()
                .filter(h -> h.getVerificationStatus() == House.VerificationStatus.PENDING)
                .toList();
        
        if (!pendingHouses.isEmpty()) {
            return HouseConflictVO.conflict(
                    HouseConflictVO.ConflictType.PENDING_APPROVAL,
                    pendingHouses,
                    "该位置有" + pendingHouses.size() + "个待审核的房屋申请"
            );
        }
        
        // 4. 检查是否有争议房屋
        List<House> disputedHouses = sameLocationHouses.stream()
                .filter(h -> h.getVerificationStatus() == House.VerificationStatus.DISPUTED)
                .toList();
        
        if (!disputedHouses.isEmpty()) {
            return HouseConflictVO.conflict(
                    HouseConflictVO.ConflictType.MULTIPLE_DISPUTES,
                    disputedHouses,
                    "该位置存在" + disputedHouses.size() + "个争议房屋"
            );
        }
        
        return HouseConflictVO.noConflict();
    }

    @Override
    @Transactional
    public void verifyHouse(HouseVerificationDTO dto) {
        House house = houseRepository.findById(dto.getHouseId())
                .orElseThrow(() -> NotFoundException.of("房屋", dto.getHouseId()));
        
        log.info("管理员{}审核房屋{}，结果：{}", dto.getReviewerName(), dto.getHouseId(), dto.getStatus());
        
        // 更新房屋状态
        switch (dto.getStatus()) {
            case "APPROVED":
                house.setVerificationStatus(House.VerificationStatus.APPROVED);
                house.setVerifiedAt(LocalDateTime.now());
                // 认证通过时，需要处理同位置的其他房屋
                handleConflictingHousesOnApproval(house);
                break;
            case "REJECTED":
                house.setVerificationStatus(House.VerificationStatus.REJECTED);
                break;
            case "DISPUTED":
                house.setVerificationStatus(House.VerificationStatus.DISPUTED);
                house.setHasDispute(true);
                break;
            default:
                throw new BusinessException("无效的审核状态：" + dto.getStatus());
        }
        
        house.setVerificationRemark(dto.getRemark());
        houseRepository.save(house);
        
        // 记录系统日志
        SystemLog sysLog = new SystemLog();
        sysLog.setUserId(house.getOwner().getId());
        sysLog.setUserType(SystemLog.UserType.ADMIN);
        sysLog.setOperation("房屋认证审核");
        sysLog.setDetail(String.format("房屋%s审核结果：%s，审核人：%s", 
                house.getUniqueKey(), dto.getStatus(), dto.getReviewerName()));
        systemLogService.addLog(sysLog);
    }

    @Override
    @Transactional
    public void handleHouseDispute(Long houseId, String action, String reason, String adminName) {
        House house = houseRepository.findById(houseId)
                .orElseThrow(() -> NotFoundException.of("房屋", houseId));
        
        log.info("管理员{}处理房屋{}争议，操作：{}", adminName, houseId, action);
        
        switch (action) {
            case "RESOLVE_APPROVE":
                // 解决争议并认证通过
                house.setVerificationStatus(House.VerificationStatus.APPROVED);
                house.setHasDispute(false);
                house.setVerifiedAt(LocalDateTime.now());
                house.setVerificationRemark("争议已解决，认证通过。原因：" + reason);
                handleConflictingHousesOnApproval(house);
                break;
            case "RESOLVE_REJECT":
                // 解决争议并拒绝
                house.setVerificationStatus(House.VerificationStatus.REJECTED);
                house.setHasDispute(false);
                house.setVerificationRemark("争议已解决，认证拒绝。原因：" + reason);
                break;
            case "MARK_DISPUTED":
                // 标记为争议状态
                house.setVerificationStatus(House.VerificationStatus.DISPUTED);
                house.setHasDispute(true);
                house.setVerificationRemark("管理员标记为争议。原因：" + reason);
                break;
            default:
                throw new BusinessException("无效的争议处理操作：" + action);
        }
        
        houseRepository.save(house);
        
        // 记录系统日志
        SystemLog sysLog = new SystemLog();
        sysLog.setUserId(house.getOwner().getId());
        sysLog.setUserType(SystemLog.UserType.ADMIN);
        sysLog.setOperation("房屋争议处理");
        sysLog.setDetail(String.format("房屋%s争议处理：%s，原因：%s，处理人：%s", 
                house.getUniqueKey(), action, reason, adminName));
        systemLogService.addLog(sysLog);
    }

    /**
     * 当房屋认证通过时，处理同位置的其他房屋
     */
    private void handleConflictingHousesOnApproval(House approvedHouse) {
        List<House> conflictingHouses = houseRepository.findByLocation(
                approvedHouse.getCommunity().getId(),
                approvedHouse.getBuilding(),
                approvedHouse.getUnit(),
                approvedHouse.getRoom()
        );
        
        for (House conflictHouse : conflictingHouses) {
            if (!conflictHouse.getId().equals(approvedHouse.getId())) {
                // 将其他同位置房屋标记为被拒绝
                conflictHouse.setVerificationStatus(House.VerificationStatus.REJECTED);
                conflictHouse.setHasDispute(false);
                conflictHouse.setVerificationRemark("同位置房屋已有其他业主认证通过");
                houseRepository.save(conflictHouse);
                
                log.info("房屋{}认证通过，自动拒绝同位置房屋{}", 
                        approvedHouse.getId(), conflictHouse.getId());
            }
        }
    }

    @Override
    public List<House> findByOwnerId(Long ownerId) {
        return houseRepository.findByOwnerId(ownerId);
    }

    @Override
    public House findById(Long id) {
        return houseRepository.findById(id).orElse(null);
    }

    @Override
    public List<House> findPendingHouses() {
        return houseRepository.findPendingHouses();
    }

    @Override
    public List<House> findDisputedHouses() {
        return houseRepository.findByHasDisputeTrue();
    }

    @Override
    public List<House> findByVerificationStatus(House.VerificationStatus status) {
        return houseRepository.findByVerificationStatus(status);
    }

    @Override
    public List<House> findByCommunityIdAndVerificationStatus(Long communityId, House.VerificationStatus status) {
        return houseRepository.findByCommunityIdAndVerificationStatus(communityId, status);
    }

    @Override
    public boolean canOwnerVote(Long ownerId) {
        List<House> approvedHouses = houseRepository.findByOwnerId(ownerId).stream()
                .filter(h -> h.getVerificationStatus() == House.VerificationStatus.APPROVED)
                .toList();
        return !approvedHouses.isEmpty();
    }

    @Override
    public int getVotingWeight(Long ownerId) {
        List<House> approvedHouses = houseRepository.findByOwnerId(ownerId).stream()
                .filter(h -> h.getVerificationStatus() == House.VerificationStatus.APPROVED)
                .toList();
        return approvedHouses.size(); // 每套认证房屋1票权重
    }
}