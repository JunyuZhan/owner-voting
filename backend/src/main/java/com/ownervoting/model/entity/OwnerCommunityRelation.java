package com.ownervoting.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "owner_community_relation",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_owner_community", 
                           columnNames = {"owner_id", "community_id"})
       })
public class OwnerCommunityRelation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    // 申请状态
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    // 申请时间
    @Column(nullable = false)
    private LocalDateTime appliedAt;

    // 审核时间
    private LocalDateTime reviewedAt;

    // 审核人
    @Column(length = 50)
    private String reviewerName;

    // 审核备注
    @Column(columnDefinition = "TEXT")
    private String reviewRemark;

    // 申请理由
    @Column(columnDefinition = "TEXT")
    private String applicationReason;

    public enum ApplicationStatus {
        PENDING,    // 待审核
        APPROVED,   // 已通过
        REJECTED    // 已拒绝
    }

    @PrePersist
    public void prePersist() {
        appliedAt = LocalDateTime.now();
    }
} 