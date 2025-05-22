package com.ownervoting.model.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OwnerVerifyStatusVO {
    private Long ownerId;
    private Boolean isVerified;
    private String status; // PENDING, APPROVED, REJECTED
    private String remark;
    private String reviewComment;
    private String reviewerName;
    private LocalDateTime reviewedAt;
} 