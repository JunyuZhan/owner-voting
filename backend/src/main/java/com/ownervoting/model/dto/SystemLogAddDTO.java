package com.ownervoting.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
public class SystemLogAddDTO {
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    @NotBlank(message = "用户类型不能为空")
    private String userType;
    @NotBlank(message = "操作不能为空")
    private String operation;
    private String ipAddress;
    private String detail;
} 