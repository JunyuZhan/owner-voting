package com.ownervoting.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
public class CommunityApplicationDTO {
    
    @NotNull(message = "业主ID不能为空")
    private Long ownerId;
    
    @NotNull(message = "小区ID不能为空")
    private Long communityId;
    
    @NotBlank(message = "申请理由不能为空")
    private String applicationReason;
    
    // 可选：提供房屋相关信息作为申请依据
    private String houseInfo;
    
    // 可选：联系方式
    private String contactInfo;
} 