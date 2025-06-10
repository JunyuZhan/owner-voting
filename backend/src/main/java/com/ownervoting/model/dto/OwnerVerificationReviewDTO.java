package com.ownervoting.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
public class OwnerVerificationReviewDTO {
    
    @NotNull(message = "业主ID不能为空")
    private Long ownerId;
    
    @NotBlank(message = "审核状态不能为空")
    private String status; // APPROVED, REJECTED
    
    private String reviewComment; // 审核意见
} 