package com.ownervoting.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "community_admin_application")
public class CommunityAdminApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 申请人信息
    @Column(nullable = false, length = 50)
    private String applicantName;

    @Column(nullable = false, length = 20)
    private String applicantPhone;

    @Column(length = 100)
    private String applicantEmail;

    @Column(length = 18)
    private String applicantIdCard;

    // 小区信息（申请创建的小区）
    @Column(nullable = false, length = 255)
    private String communityName;

    @Column(nullable = false, length = 255)
    private String communityAddress;

    @Column(columnDefinition = "TEXT")
    private String communityDescription;

    // 申请理由和资质证明
    @Column(nullable = false, columnDefinition = "TEXT")
    private String applicationReason;

    @Column(columnDefinition = "TEXT")
    private String qualificationProof;

    @Column(length = 255)
    private String businessLicense;

    // 申请状态
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    // 审核信息
    private LocalDateTime reviewedAt;

    @Column(length = 50)
    private String reviewerName;

    @Column(columnDefinition = "TEXT")
    private String reviewRemark;

    // 创建的小区ID（审核通过后）
    @Column(name = "created_community_id")
    private Long createdCommunityId;

    // 创建的管理员用户ID（审核通过后）
    @Column(name = "created_admin_user_id")
    private Long createdAdminUserId;

    // 时间戳
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ApplicationStatus {
        PENDING,    // 待审核
        APPROVED,   // 已通过
        REJECTED    // 已拒绝
    }
} 