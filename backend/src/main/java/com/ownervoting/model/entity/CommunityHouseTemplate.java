package com.ownervoting.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "community_house_template")
public class CommunityHouseTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id", nullable = false)
    private Community community;

    // 房屋编号格式模板，如："{building}栋{unit}单元{floor}楼{room}号"
    @Column(length = 200, nullable = false)
    private String numberFormat;

    // 楼栋范围
    @Column(length = 100)
    private String buildingRange; // 如："1-10" 或 "A,B,C"

    // 单元范围  
    @Column(length = 100)
    private String unitRange; // 如："1-3" 或 为空（无单元）

    // 楼层范围
    @Column(length = 100)
    private String floorRange; // 如："1-30"

    // 房间范围
    @Column(length = 100)
    private String roomRange; // 如："01-04" 或 "1-4"

    // 是否启用
    @Column(nullable = false)
    private Boolean enabled = true;

    // 创建时间
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // 更新时间
    private LocalDateTime updatedAt;

    // 创建人
    @Column(length = 50)
    private String createdBy;

    // 备注说明
    @Column(columnDefinition = "TEXT")
    private String remark;

    @PrePersist
    public void prePersist() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 生成房屋编号
     */
    public String generateHouseNumber(String building, String unit, String floor, String room) {
        String result = numberFormat;
        result = result.replace("{building}", building != null ? building : "");
        result = result.replace("{unit}", unit != null ? unit : "");
        result = result.replace("{floor}", floor != null ? floor : "");
        result = result.replace("{room}", room != null ? room : "");
        return result;
    }
} 