package com.ownervoting.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class VoteRecordAddDTO {
    @NotNull(message = "选项ID不能为空")
    private Long optionId;
    
    @NotNull(message = "投票人ID不能为空")
    private Long voterId;
    
    @NotNull(message = "房产ID不能为空")
    private Long houseId;
    
    private String ipAddress;
    private String deviceInfo;
} 