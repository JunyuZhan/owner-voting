package com.ownervoting.controller;

import com.ownervoting.model.dto.HouseRegistrationDTO;
import com.ownervoting.model.dto.HouseVerificationDTO;
import com.ownervoting.model.entity.House;
import com.ownervoting.model.dto.HouseAddDTO;
import com.ownervoting.model.vo.ApiResponse;
import com.ownervoting.model.vo.HouseVO;
import com.ownervoting.model.vo.HouseConflictVO;
import com.ownervoting.service.HouseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/house")
@Validated
public class HouseController {

    @Autowired
    private HouseService houseService;

    @PostMapping("/add")
    public ApiResponse<HouseVO> addHouse(@Valid @RequestBody HouseAddDTO dto) {
        House house = new House();
        house.setBuilding(dto.getBuilding());
        house.setUnit(dto.getUnit());
        house.setRoom(dto.getRoom());
        house.setAddress(dto.getAddress());
        house.setArea(dto.getArea());
        house.setCertificateNumber(dto.getCertificateNumber());
        house.setIsPrimary(dto.getIsPrimary());
        // owner和community应通过Service查找并set
        House saved = houseService.addHouse(house);
        HouseVO vo = toVO(saved);
        return ApiResponse.success(vo);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteHouse(@PathVariable Long id) {
        houseService.deleteHouse(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/detail/{id}")
    public ApiResponse<HouseVO> getById(@PathVariable Long id) {
        House house = houseService.findById(id);
        if (house == null) return ApiResponse.success(null);
        return ApiResponse.success(toVO(house));
    }

    @GetMapping("/by-owner/{ownerId}")
    public ApiResponse<List<HouseVO>> getByOwnerId(@PathVariable Long ownerId) {
        List<House> list = houseService.findByOwnerId(ownerId);
        return ApiResponse.success(list.stream().map(this::toVO).toList());
    }

    private HouseVO toVO(House house) {
        HouseVO vo = new HouseVO();
        vo.setId(house.getId());
        vo.setOwnerId(house.getOwner() != null ? house.getOwner().getId() : null);
        vo.setCommunityId(house.getCommunity() != null ? house.getCommunity().getId() : null);
        vo.setBuilding(house.getBuilding());
        vo.setUnit(house.getUnit());
        vo.setRoom(house.getRoom());
        vo.setAddress(house.getAddress());
        vo.setArea(house.getArea());
        vo.setCertificateNumber(house.getCertificateNumber());
        vo.setIsPrimary(house.getIsPrimary());
        return vo;
    }

    /**
     * 业主注册房屋
     */
    @PostMapping("/register")
    @PreAuthorize("hasRole('OWNER')")
    public ApiResponse<House> registerHouse(@Valid @RequestBody HouseRegistrationDTO dto) {
        log.info("业主{}注册房屋：{}-{}-{}-{}", 
                dto.getOwnerId(), dto.getCommunityId(), dto.getBuilding(), dto.getUnit(), dto.getRoom());
        
        House house = houseService.registerHouse(dto);
        return ApiResponse.success(house);
    }

    /**
     * 检查房屋位置冲突
     */
    @GetMapping("/check-conflict")
    public ApiResponse<HouseConflictVO> checkHouseConflict(
            @RequestParam Long communityId,
            @RequestParam String building,
            @RequestParam(required = false) String unit,
            @RequestParam String room) {
        
        HouseConflictVO conflict = houseService.checkHouseConflict(communityId, building, unit, room);
        return ApiResponse.success(conflict);
    }

    /**
     * 获取业主的房屋列表
     */
    @GetMapping("/owner/{ownerId}")
    @PreAuthorize("hasRole('OWNER') or hasAnyRole('SYSTEM_ADMIN', 'COMMUNITY_ADMIN')")
    public ApiResponse<List<House>> getOwnerHouses(@PathVariable Long ownerId) {
        List<House> houses = houseService.findByOwnerId(ownerId);
        return ApiResponse.success(houses);
    }

    /**
     * 获取房屋详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('OWNER') or hasAnyRole('SYSTEM_ADMIN', 'COMMUNITY_ADMIN')")
    public ApiResponse<House> getHouseDetail(@PathVariable Long id) {
        House house = houseService.findById(id);
        if (house == null) {
            return ApiResponse.error(404, "房屋不存在");
        }
        return ApiResponse.success(house);
    }

    /**
     * 检查业主投票资格
     */
    @GetMapping("/voting-eligibility/{ownerId}")
    public ApiResponse<Map<String, Object>> checkVotingEligibility(@PathVariable Long ownerId) {
        boolean canVote = houseService.canOwnerVote(ownerId);
        int votingWeight = houseService.getVotingWeight(ownerId);
        
        return ApiResponse.success(Map.of(
                "canVote", canVote,
                "votingWeight", votingWeight,
                "message", canVote ? "可以参与投票" : "需要至少有一套已认证的房屋才能参与投票"
        ));
    }

    /**
     * 修改房屋信息（仅限未审核或被拒绝的房屋）
     */
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('OWNER') or hasAnyRole('SYSTEM_ADMIN', 'COMMUNITY_ADMIN')")
    public ApiResponse<House> updateHouse(@PathVariable Long id, @Valid @RequestBody HouseRegistrationDTO dto) {
        log.info("修改房屋{}信息：面积{}", id, dto.getArea());
        
        House existingHouse = houseService.findById(id);
        if (existingHouse == null) {
            return ApiResponse.error(404, "房屋不存在");
        }
        
        // 检查权限：只能修改自己的房屋，或管理员可以修改
        // TODO: 添加权限验证逻辑
        
        // 检查房屋状态：只能修改未审核或被拒绝的房屋
        House.VerificationStatus status = existingHouse.getVerificationStatus();
        if (status == House.VerificationStatus.APPROVED) {
            return ApiResponse.error(400, "已认证的房屋信息不能修改");
        }
        if (status == House.VerificationStatus.DISPUTED) {
            return ApiResponse.error(400, "存在争议的房屋信息不能修改");
        }
        
        // 只允许修改待审核(PENDING)或被拒绝(REJECTED)的房屋
        if (status != House.VerificationStatus.PENDING && status != House.VerificationStatus.REJECTED) {
            return ApiResponse.error(400, "当前状态的房屋信息不能修改");
        }
        
        existingHouse.setArea(dto.getArea());
        existingHouse.setCertificateNumber(dto.getCertificateNumber());
        existingHouse.setAddress(dto.getAddress());
        
        // 如果是被拒绝的房屋，修改后重置为待审核状态
        if (status == House.VerificationStatus.REJECTED) {
            existingHouse.setVerificationStatus(House.VerificationStatus.PENDING);
            existingHouse.setVerificationRemark("房屋信息已修改，重新提交审核");
            log.info("被拒绝的房屋{}信息已修改，状态重置为待审核", id);
        }
        
        House updated = houseService.updateHouse(existingHouse);
        return ApiResponse.success(updated);
    }

    // ==================== 管理员接口 ====================

    /**
     * 获取待审核的房屋列表
     */
    @GetMapping("/admin/pending")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'COMMUNITY_ADMIN')")
    public ApiResponse<List<House>> getPendingHouses() {
        List<House> houses = houseService.findPendingHouses();
        return ApiResponse.success(houses);
    }

    /**
     * 获取存在争议的房屋列表
     */
    @GetMapping("/admin/disputed")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'COMMUNITY_ADMIN')")
    public ApiResponse<List<House>> getDisputedHouses() {
        List<House> houses = houseService.findDisputedHouses();
        return ApiResponse.success(houses);
    }

    /**
     * 房屋认证审核
     */
    @PostMapping("/admin/verify")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'COMMUNITY_ADMIN')")
    public ApiResponse<Void> verifyHouse(@Valid @RequestBody HouseVerificationDTO dto) {
        log.info("管理员{}审核房屋{}，状态：{}", dto.getReviewerName(), dto.getHouseId(), dto.getStatus());
        
        houseService.verifyHouse(dto);
        return ApiResponse.success(null);
    }

    /**
     * 处理房屋争议
     */
    @PostMapping("/admin/dispute/{houseId}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'COMMUNITY_ADMIN')")
    public ApiResponse<Void> handleDispute(
            @PathVariable Long houseId,
            @RequestBody Map<String, String> request) {
        
        String action = request.get("action");
        String reason = request.get("reason");
        String adminName = request.get("adminName");
        
        log.info("管理员{}处理房屋{}争议，操作：{}", adminName, houseId, action);
        
        houseService.handleHouseDispute(houseId, action, reason, adminName);
        return ApiResponse.success(null);
    }

    /**
     * 根据认证状态获取房屋列表
     */
    @GetMapping("/admin/by-status")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'COMMUNITY_ADMIN')")
    public ApiResponse<List<House>> getHousesByStatus(@RequestParam String status) {
        House.VerificationStatus verificationStatus = House.VerificationStatus.valueOf(status);
        List<House> houses = houseService.findByVerificationStatus(verificationStatus);
        return ApiResponse.success(houses);
    }
} 