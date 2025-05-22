package com.ownervoting.model.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class SystemLogVO {
    private Long id;
    private Long userId;
    private String userType;
    private String operation;
    private String ipAddress;
    private String detail;
    private LocalDateTime createdAt;
} 