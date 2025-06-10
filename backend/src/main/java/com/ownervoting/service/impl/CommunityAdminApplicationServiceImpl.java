package com.ownervoting.service.impl;

import com.ownervoting.exception.BusinessException;
import com.ownervoting.exception.NotFoundException;
import com.ownervoting.model.entity.AdminUser;
import com.ownervoting.model.entity.Community;
import com.ownervoting.model.entity.CommunityAdminApplication;
import com.ownervoting.model.entity.SystemLog;
import com.ownervoting.model.dto.CommunityAdminApplicationDTO;
import com.ownervoting.model.dto.CommunityAdminApplicationReviewDTO;
import com.ownervoting.repository.AdminUserRepository;
import com.ownervoting.repository.CommunityRepository;
import com.ownervoting.repository.CommunityAdminApplicationRepository;
import com.ownervoting.service.CommunityAdminApplicationService;
import com.ownervoting.service.SystemLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class CommunityAdminApplicationServiceImpl implements CommunityAdminApplicationService {

    @Autowired
    private CommunityAdminApplicationRepository applicationRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Autowired
    private SystemLogService systemLogService;

    @Override
    @Transactional
    public CommunityAdminApplication submitApplication(CommunityAdminApplicationDTO dto) {
        log.info("提交小区管理员申请: 申请人={}, 小区={}", dto.getApplicantName(), dto.getCommunityName());

        // 1. 检查是否已有相同小区的待审核或已通过申请
        Optional<CommunityAdminApplication> existingPending = 
                applicationRepository.findByCommunityNameAndStatus(
                        dto.getCommunityName(), CommunityAdminApplication.ApplicationStatus.PENDING);
        
        if (existingPending.isPresent()) {
            throw new BusinessException("该小区已有待审核的管理员申请，请等待审核结果");
        }

        Optional<CommunityAdminApplication> existingApproved = 
                applicationRepository.findByCommunityNameAndStatus(
                        dto.getCommunityName(), CommunityAdminApplication.ApplicationStatus.APPROVED);
        
        if (existingApproved.isPresent()) {
            throw new BusinessException("该小区已有管理员，无法重复申请");
        }

        // 2. 检查申请人是否已有管理员账号
        Optional<AdminUser> existingAdmin = adminUserRepository.findByUsername(dto.getApplicantPhone());
        if (existingAdmin.isPresent()) {
            throw new BusinessException("该手机号已注册为管理员账号");
        }

        // 3. 创建申请记录
        CommunityAdminApplication application = new CommunityAdminApplication();
        application.setApplicantName(dto.getApplicantName());
        application.setApplicantPhone(dto.getApplicantPhone());
        application.setApplicantEmail(dto.getApplicantEmail());
        application.setApplicantIdCard(dto.getApplicantIdCard());
        application.setCommunityName(dto.getCommunityName());
        application.setCommunityAddress(dto.getCommunityAddress());
        application.setCommunityDescription(dto.getCommunityDescription());
        application.setApplicationReason(dto.getApplicationReason());
        application.setQualificationProof(dto.getQualificationProof());
        application.setBusinessLicense(dto.getBusinessLicense());
        
        CommunityAdminApplication saved = applicationRepository.save(application);

        // 4. 记录系统日志
        SystemLog sysLog = new SystemLog();
        sysLog.setUserId(0L); // 公开申请，无用户ID
        sysLog.setUserType(SystemLog.UserType.ADMIN);
        sysLog.setOperation("提交小区管理员申请");
        sysLog.setDetail(String.format("申请人%s提交小区%s的管理员申请", 
                dto.getApplicantName(), dto.getCommunityName()));
        systemLogService.addLog(sysLog);

        return saved;
    }

    @Override
    @Transactional
    public void reviewApplication(CommunityAdminApplicationReviewDTO dto) {
        log.info("审核小区管理员申请: 申请ID={}, 状态={}", dto.getApplicationId(), dto.getStatus());

        // 1. 查找申请记录
        CommunityAdminApplication application = applicationRepository.findById(dto.getApplicationId())
                .orElseThrow(() -> NotFoundException.of("申请记录", dto.getApplicationId()));

        if (application.getStatus() != CommunityAdminApplication.ApplicationStatus.PENDING) {
            throw new BusinessException("该申请已被处理，无法重复审核");
        }

        // 2. 更新申请状态
        switch (dto.getStatus()) {
            case "APPROVED":
                application.setStatus(CommunityAdminApplication.ApplicationStatus.APPROVED);
                approveApplication(application, dto);
                break;
            case "REJECTED":
                application.setStatus(CommunityAdminApplication.ApplicationStatus.REJECTED);
                break;
            default:
                throw new BusinessException("无效的审核状态：" + dto.getStatus());
        }

        application.setReviewedAt(LocalDateTime.now());
        application.setReviewerName(dto.getReviewerName());
        application.setReviewRemark(dto.getReviewRemark());

        applicationRepository.save(application);

        // 3. 记录系统日志
        SystemLog sysLog = new SystemLog();
        sysLog.setUserId(0L); 
        sysLog.setUserType(SystemLog.UserType.ADMIN);
        sysLog.setOperation("小区管理员申请审核");
        sysLog.setDetail(String.format("审核申请人%s的小区%s管理员申请，结果：%s", 
                application.getApplicantName(), application.getCommunityName(), dto.getStatus()));
        systemLogService.addLog(sysLog);
    }

    /**
     * 审核通过时创建小区和管理员账号
     */
    private void approveApplication(CommunityAdminApplication application, CommunityAdminApplicationReviewDTO dto) {
        // 1. 创建小区
        Community community = new Community();
        community.setName(application.getCommunityName());
        community.setAddress(application.getCommunityAddress());
        community.setDescription(application.getCommunityDescription());
        community.setCreatedAt(LocalDateTime.now());
        
        Community savedCommunity = communityRepository.save(community);
        application.setCreatedCommunityId(savedCommunity.getId());

        // 2. 创建管理员账号
        AdminUser adminUser = new AdminUser();
        adminUser.setUsername(dto.getAdminUsername() != null ? dto.getAdminUsername() : application.getApplicantPhone());
        adminUser.setPasswordHash(dto.getAdminPassword() != null ? dto.getAdminPassword() : "123456"); // 默认密码
        adminUser.setName(application.getApplicantName());
        adminUser.setPhone(application.getApplicantPhone());
        adminUser.setEmail(application.getApplicantEmail());
        adminUser.setRole(AdminUser.Role.COMMUNITY_ADMIN);
        adminUser.setCommunity(savedCommunity);
        adminUser.setEnabled(true);
        adminUser.setCreatedAt(LocalDateTime.now());
        adminUser.setUpdatedAt(LocalDateTime.now());

        AdminUser savedAdmin = adminUserRepository.save(adminUser);
        application.setCreatedAdminUserId(savedAdmin.getId());

        log.info("成功创建小区{}和管理员账号{}", savedCommunity.getName(), savedAdmin.getUsername());
    }

    @Override
    public List<CommunityAdminApplication> findPendingApplications() {
        return applicationRepository.findPendingApplications();
    }

    @Override
    public List<CommunityAdminApplication> findApplicationsByStatus(String status) {
        if (status == null || status.isEmpty()) {
            return applicationRepository.findAll();
        }
        
        CommunityAdminApplication.ApplicationStatus applicationStatus;
        try {
            applicationStatus = CommunityAdminApplication.ApplicationStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BusinessException("无效的申请状态：" + status);
        }
        
        return applicationRepository.findByStatusOrderByCreatedAtDesc(applicationStatus);
    }

    @Override
    public CommunityAdminApplication findById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("申请记录", id));
    }

    @Override
    public List<CommunityAdminApplication> findByApplicantPhone(String phone) {
        return applicationRepository.findByApplicantPhoneOrderByCreatedAtDesc(phone);
    }

    @Override
    public Map<String, Long> getApplicationStatistics() {
        Map<String, Long> statistics = new HashMap<>();
        statistics.put("PENDING", applicationRepository.countByStatus(CommunityAdminApplication.ApplicationStatus.PENDING));
        statistics.put("APPROVED", applicationRepository.countByStatus(CommunityAdminApplication.ApplicationStatus.APPROVED));
        statistics.put("REJECTED", applicationRepository.countByStatus(CommunityAdminApplication.ApplicationStatus.REJECTED));
        statistics.put("TOTAL", applicationRepository.count());
        return statistics;
    }
} 