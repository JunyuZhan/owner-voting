package com.ownervoting.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
public class HouseVerificationDTO {
    
    @NotNull(message = "房屋ID不能为空")
    private Long houseId;
    
    @NotBlank(message = "审核状态不能为空")
    private String status; // APPROVED, REJECTED, DISPUTED
    
    @NotBlank(message = "审核人不能为空")
    private String reviewerName;
    
    private String remark; // 审核备注
    
    // 如果是争议状态，需要的额外信息
    private String disputeReason; // 争议原因
    private Long conflictHouseId; // 冲突的房屋ID
} 