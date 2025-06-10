package com.ownervoting.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

@Data
public class HouseRegistrationDTO {
    
    @NotNull(message = "业主ID不能为空")
    private Long ownerId;
    
    @NotNull(message = "小区ID不能为空") 
    private Long communityId;
    
    @NotBlank(message = "楼栋号不能为空")
    private String building;
    
    private String unit; // 单元号可以为空（如别墅等）
    
    @NotBlank(message = "房间号不能为空")
    private String room;
    
    private String address; // 完整地址
    
    @DecimalMin(value = "0.1", message = "面积必须大于0")
    private BigDecimal area;
    
    @NotBlank(message = "房产证号不能为空")
    private String certificateNumber;
    
    // 房产证照片文件ID
    @NotNull(message = "房产证照片不能为空")
    private Long certificateFileId;
    
    // 是否设为主要房屋
    private Boolean isPrimary = false;
    
    // 备注信息
    private String remark;
} 