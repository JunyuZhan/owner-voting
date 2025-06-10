package com.ownervoting.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
public class CommunityApplicationReviewDTO {
    
    @NotNull(message = "申请ID不能为空")
    private Long applicationId;
    
    @NotBlank(message = "审核状态不能为空")
    private String status; // APPROVED, REJECTED
    
    @NotBlank(message = "审核人不能为空")
    private String reviewerName;
    
    private String reviewRemark; // 审核备注
} 