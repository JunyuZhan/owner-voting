package com.ownervoting.model.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class CommunityVO {
    private Long id;
    private String name;
    private String address;
    private String description;
    private LocalDateTime createdAt;
    
    // 业主在该小区的状态
    private String ownerStatus; // PENDING, APPROVED, REJECTED, NOT_APPLIED
    private LocalDateTime appliedAt;
    private LocalDateTime reviewedAt;
    private String reviewRemark;
    
    // 小区统计信息
    private Long totalOwners; // 已认证业主数量
    private Long totalHouses; // 房屋总数
} 