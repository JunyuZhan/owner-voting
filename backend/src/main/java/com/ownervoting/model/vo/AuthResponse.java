package com.ownervoting.model.vo;

import lombok.Data;

@Data
public class AuthResponse {
    private String token;
    private long expiresIn;
    private Object userInfo;
} 