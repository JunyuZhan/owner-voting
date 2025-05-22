package com.ownervoting.model.vo;

import lombok.Data;

@Data
public class OwnerVO {
    private Long id;
    private String phone;
    private String name;
    private Boolean isVerified;
    private String status;
} 