package com.ownervoting.model.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OwnerVO {
    private Long id;
    private String phone;
    private String name;
    private Boolean isVerified;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
} 