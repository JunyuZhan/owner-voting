package com.ownervoting.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;

@Data
public class HouseAddDTO {
    @NotNull(message = "业主ID不能为空")
    private Long ownerId;
    @NotNull(message = "小区ID不能为空")
    private Long communityId;
    @NotBlank(message = "楼栋不能为空")
    private String building;
    @NotBlank(message = "单元不能为空")
    private String unit;
    @NotBlank(message = "房号不能为空")
    private String room;
    @NotBlank(message = "地址不能为空")
    private String address;
    @NotNull(message = "面积不能为空")
    private BigDecimal area;
    @NotBlank(message = "证书编号不能为空")
    private String certificateNumber;
    private Boolean isPrimary = false;
} 