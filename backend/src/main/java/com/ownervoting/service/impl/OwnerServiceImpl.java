package com.ownervoting.service.impl;

import com.ownervoting.exception.BusinessException;
import com.ownervoting.exception.NotFoundException;
import com.ownervoting.model.entity.Owner;
import com.ownervoting.repository.OwnerRepository;
import com.ownervoting.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.ownervoting.model.dto.OwnerVerifyDTO;
import com.ownervoting.model.vo.OwnerVerifyStatusVO;
import com.ownervoting.model.entity.FileUpload;
import com.ownervoting.service.FileUploadService;
import org.springframework.beans.factory.annotation.Qualifier;
import com.ownervoting.model.dto.OwnerReviewDTO;
import com.ownervoting.model.entity.ReviewLog;
import com.ownervoting.service.ReviewLogService;
import com.ownervoting.service.SystemLogService;
import com.ownervoting.model.entity.SystemLog;
import com.ownervoting.model.dto.OwnerAddDTO;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.UUID;

@Service
public class OwnerServiceImpl implements OwnerService {

    @Autowired
    private OwnerRepository ownerRepository;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private ReviewLogService reviewLogService;

    @Autowired
    private SystemLogService systemLogService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public Owner registerOwner(Owner owner) {
        // 检查手机号是否已被注册
        if (ownerRepository.findByPhone(owner.getPhone()) != null) {
            throw new BusinessException("该手机号已被注册");
        }
        // 可根据实际需求添加更多校验逻辑
        return ownerRepository.save(owner);
    }
    
    @Override
    @Transactional
    public Owner addOwner(OwnerAddDTO dto) {
        // 1. 检查手机号是否已被注册
        if (ownerRepository.findByPhone(dto.getPhone()) != null) {
            throw new BusinessException("该手机号已被注册");
        }
        
        // 2. 创建Owner实体
        Owner owner = new Owner();
        owner.setName(dto.getName());
        owner.setPhone(dto.getPhone());
        owner.setIdCard(dto.getIdCard());
        
        // 3. 处理密码：如果没有提供密码，则生成默认密码
        String password = dto.getPassword();
        if (password == null || password.trim().isEmpty()) {
            // 生成默认密码：手机号后6位
            password = dto.getPhone().substring(5);
        }
        owner.setPasswordHash(passwordEncoder.encode(password));
        
        // 4. 设置初始状态
        owner.setIsVerified(dto.getIsVerified() != null ? dto.getIsVerified() : false);
        owner.setStatus(owner.getIsVerified() ? "APPROVED" : "PENDING");
        
        // 5. 保存业主
        Owner saved = ownerRepository.save(owner);
        
        // 6. 记录系统日志
        SystemLog sysLog = new SystemLog();
        sysLog.setUserId(saved.getId());
        sysLog.setUserType(SystemLog.UserType.OWNER);
        sysLog.setOperation("管理员新增业主");
        sysLog.setDetail("管理员创建业主账户：" + saved.getName() + "(" + saved.getPhone() + ")，默认密码：" + password);
        systemLogService.addLog(sysLog);
        
        return saved;
    }

    @Override
    public Owner findByPhone(String phone) {
        return ownerRepository.findByPhone(phone);
    }

    @Override
    public Owner findById(Long id) {
        return ownerRepository.findById(id).orElse(null);
    }

    @Override
    public List<Owner> findAll() {
        return ownerRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteOwner(Long id) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("业主", id));
        ownerRepository.delete(owner);
        
        // 系统日志
        SystemLog sysLog = new SystemLog();
        sysLog.setUserId(id);
        sysLog.setUserType(SystemLog.UserType.OWNER);
        sysLog.setOperation("删除业主");
        sysLog.setDetail("管理员删除业主：" + owner.getName());
        systemLogService.addLog(sysLog);
    }

    @Override
    @Transactional
    public void submitVerifyRequest(OwnerVerifyDTO dto) {
        Owner owner = ownerRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> NotFoundException.of("业主", dto.getOwnerId()));
        
        // 检查是否可以提交认证申请
        if ("APPROVED".equals(owner.getStatus())) {
            throw new BusinessException("已认证通过，无需重复认证");
        }
        
        if ("PENDING".equals(owner.getStatus())) {
            throw new BusinessException("认证申请审核中，请耐心等待");
        }
        
        // 设置为待审核状态
        owner.setStatus("PENDING");
        owner.setIsVerified(false);
        ownerRepository.save(owner);
        
        // 日志埋点
        SystemLog log = new SystemLog();
        log.setUserId(owner.getId());
        log.setUserType(SystemLog.UserType.OWNER);
        log.setOperation("认证申请");
        log.setDetail("业主提交实名认证申请");
        systemLogService.addLog(log);
    }

    @Override
    public OwnerVerifyStatusVO getVerifyStatus(Long ownerId) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> NotFoundException.of("业主", ownerId));
                
        OwnerVerifyStatusVO vo = new OwnerVerifyStatusVO();
        vo.setOwnerId(owner.getId());
        vo.setIsVerified(owner.getIsVerified());
        vo.setStatus(owner.getStatus());
        // 可扩展：查询最新审核意见、remark等
        return vo;
    }

    @Override
    @Transactional
    public void reviewVerifyRequest(OwnerReviewDTO dto) {
        Owner owner = ownerRepository.findById(dto.getOwnerId())
                .orElseThrow(() -> NotFoundException.of("业主", dto.getOwnerId()));
                
        if (!"PENDING".equals(owner.getStatus())) {
            throw new BusinessException("当前状态不可审核，状态：" + owner.getStatus());
        }
        
        // 验证审核状态值的合法性
        if (!("APPROVED".equals(dto.getStatus()) || "REJECTED".equals(dto.getStatus()))) {
            throw new BusinessException("审核状态非法，只能是 APPROVED 或 REJECTED");
        }
        
        // 设置业主状态
        if ("APPROVED".equals(dto.getStatus())) {
            owner.setStatus("APPROVED");
            owner.setIsVerified(true);
        } else {
            owner.setStatus("REJECTED");
            owner.setIsVerified(false);
        }
        ownerRepository.save(owner);
        
        // 写入审核日志
        ReviewLog log = new ReviewLog();
        log.setOwner(owner);
        log.setReviewerName(dto.getReviewerName());
        log.setStatus("APPROVED".equals(dto.getStatus()) ? ReviewLog.Status.APPROVED : ReviewLog.Status.REJECTED);
        log.setComment(dto.getComment());
        reviewLogService.addReviewLog(log);
        
        // 系统日志
        SystemLog sysLog = new SystemLog();
        sysLog.setUserId(owner.getId());
        sysLog.setUserType(SystemLog.UserType.OWNER);
        sysLog.setOperation("认证审核");
        sysLog.setDetail("业主认证审核，结果：" + dto.getStatus() + ", 审核人：" + dto.getReviewerName());
        systemLogService.addLog(sysLog);
    }

    @Override
    @Transactional
    public void reviewVerification(Long ownerId, String status, String reviewComment) {
        Owner owner = ownerRepository.findById(ownerId)
                .orElseThrow(() -> NotFoundException.of("业主", ownerId));
                
        if (!"PENDING".equals(owner.getStatus())) {
            throw new BusinessException("当前状态不可审核，状态：" + owner.getStatus());
        }
        
        // 验证审核状态值的合法性
        if (!("APPROVED".equals(status) || "REJECTED".equals(status))) {
            throw new BusinessException("审核状态非法，只能是 APPROVED 或 REJECTED");
        }
        
        // 设置业主状态
        if ("APPROVED".equals(status)) {
            owner.setStatus("APPROVED");
            owner.setIsVerified(true);
        } else {
            owner.setStatus("REJECTED");
            owner.setIsVerified(false);
        }
        ownerRepository.save(owner);
        
        // 写入审核日志
        ReviewLog log = new ReviewLog();
        log.setOwner(owner);
        log.setReviewerName("管理员"); // 可以从SecurityContext获取当前用户
        log.setStatus("APPROVED".equals(status) ? ReviewLog.Status.APPROVED : ReviewLog.Status.REJECTED);
        log.setComment(reviewComment);
        reviewLogService.addReviewLog(log);
        
        // 系统日志
        SystemLog sysLog = new SystemLog();
        sysLog.setUserId(owner.getId());
        sysLog.setUserType(SystemLog.UserType.OWNER);
        sysLog.setOperation("认证审核");
        sysLog.setDetail("业主认证审核，结果：" + status + ", 审核意见：" + reviewComment);
        systemLogService.addLog(sysLog);
    }
} 