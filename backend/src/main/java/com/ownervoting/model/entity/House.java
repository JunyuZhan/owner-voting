package com.ownervoting.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "house", 
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_house_location", 
                           columnNames = {"community_id", "building", "unit", "room"})
       })
public class House {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    @Column(length = 50, nullable = false)
    private String building;

    @Column(length = 50)
    private String unit;

    @Column(length = 50, nullable = false)
    private String room;

    @Column(length = 255)
    private String address;

    private BigDecimal area;

    @Column(length = 100)
    private String certificateNumber;

    private Boolean isPrimary = false;

    // 房屋认证状态
    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING;

    // 是否存在争议
    @Column(nullable = false)
    private Boolean hasDispute = false;

    // 创建时间
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 更新时间
    private LocalDateTime updatedAt;

    // 认证时间
    private LocalDateTime verifiedAt;

    // 认证备注
    @Column(columnDefinition = "TEXT")
    private String verificationRemark;

    @PrePersist
    public void prePersist() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum VerificationStatus {
        PENDING,    // 待认证
        APPROVED,   // 已认证
        REJECTED,   // 认证被拒
        DISPUTED    // 存在争议
    }

    /**
     * 生成房屋唯一标识符
     */
    public String getUniqueKey() {
        return String.format("%d-%s-%s-%s", 
            community.getId(), building, unit != null ? unit : "", room);
    }

    public Owner getOwner() {
        return owner;
    }
} 