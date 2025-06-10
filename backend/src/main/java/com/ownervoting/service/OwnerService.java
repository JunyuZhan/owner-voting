package com.ownervoting.service;

import com.ownervoting.model.entity.Owner;
import java.util.List;

public interface OwnerService {
    Owner registerOwner(Owner owner);
    Owner findByPhone(String phone);
    Owner findById(Long id);
    List<Owner> findAll();
    void deleteOwner(Long id);
    
    // 管理员新增业主
    Owner addOwner(com.ownervoting.model.dto.OwnerAddDTO dto);
    
    // 业主实名认证申请
    void submitVerifyRequest(com.ownervoting.model.dto.OwnerVerifyDTO dto);
    // 查询业主认证状态
    com.ownervoting.model.vo.OwnerVerifyStatusVO getVerifyStatus(Long ownerId);
    // 业主认证审核
    void reviewVerifyRequest(com.ownervoting.model.dto.OwnerReviewDTO dto);
    // 业主认证审核（管理员用）
    void reviewVerification(Long ownerId, String status, String reviewComment);
} 