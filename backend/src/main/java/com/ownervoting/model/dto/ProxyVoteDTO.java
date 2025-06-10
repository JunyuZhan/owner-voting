package com.ownervoting.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class ProxyVoteDTO {
    
    @NotNull(message = "投票议题ID不能为空")
    private Long topicId;
    
    @NotNull(message = "投票选项ID不能为空")
    private Long optionId;
    
    @NotNull(message = "业主ID不能为空")
    private Long voterId;
    
    @NotNull(message = "房屋ID不能为空")
    private Long houseId;
    
    @NotBlank(message = "代理人姓名不能为空")
    private String proxyName;
    
    @NotBlank(message = "代理原因不能为空")
    private String reason; // OFFLINE_REQUEST, TECHNICAL_ISSUE, DISABILITY, OTHER
    
    private String remark; // 备注说明
    
    private BigDecimal voteWeight; // 投票权重，默认为1
    
    private String ipAddress; // IP地址
}