package com.ownervoting.service;

import com.ownervoting.model.entity.Owner;

public interface OwnerService {
    Owner registerOwner(Owner owner);
    Owner findByPhone(String phone);
    Owner findById(Long id);
    // 业主实名认证申请
    void submitVerifyRequest(com.ownervoting.model.dto.OwnerVerifyDTO dto);
    // 查询业主认证状态
    com.ownervoting.model.vo.OwnerVerifyStatusVO getVerifyStatus(Long ownerId);
    // 业主认证审核
    void reviewVerifyRequest(com.ownervoting.model.dto.OwnerReviewDTO dto);
} 