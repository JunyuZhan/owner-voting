package com.ownervoting.model.dto;

import lombok.Data;

@Data
public class AuthRequest {
    private String phone;
    private String code;
    private String username;
    private String password;
} 