package com.ownervoting.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class AdminUserAddDTO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "密码不能为空")
    private String password;
    private String name;
    private String phone;
    private String email;
    @NotBlank(message = "角色不能为空")
    private String role;
    private Long communityId;
} 