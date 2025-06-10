package com.ownervoting.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "advertisement")
public class Advertisement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private AdType type;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "link_url")
    private String linkUrl;

    @Column(name = "baidu_cpro_id")
    private String baiduCproId;
    
    @Column(name = "tencent_app_id")
    private String tencentAppId;
    
    @Column(name = "tencent_placement_id")
    private String tencentPlacementId;
    
    @Column(name = "bytedance_app_id")
    private String bytedanceAppId;
    
    @Column(name = "bytedance_slot_id")
    private String bytedanceSlotId;
    
    @Column(name = "alimama_pid")
    private String alimamaPid;
    
    @Column(name = "qihoo_pos_id")
    private String qihooPosId;
    
    @Column(name = "sogou_app_id")
    private String sogouAppId;
    
    @Column(name = "jd_union_id")
    private String jdUnionId;
    
    @Column(name = "pdd_app_key")
    private String pddAppKey;

    @Column(name = "width")
    private Integer width;

    @Column(name = "height")
    private Integer height;

    @Column(name = "weight", nullable = false)
    private Integer weight = 1;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @Column(name = "end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    @Column(name = "click_count")
    private Long clickCount = 0L;

    @Column(name = "view_count")
    private Long viewCount = 0L;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "description")
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum AdType {
        BANNER("横幅广告"),
        BAIDU("百度联盟"),
        GOOGLE("谷歌AdSense"),
        TENCENT("腾讯优量汇"),
        BYTEDANCE("字节跳动穿山甲"),
        ALIMAMA("阿里妈妈"),
        QIHOO("360广告"),
        SOGOU("搜狗广告"),
        JD("京东联盟"),
        PDD("拼多多推广");

        private final String description;

        AdType(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    // 检查广告是否在有效期内
    public boolean isInValidPeriod() {
        LocalDateTime now = LocalDateTime.now();
        if (startTime != null && now.isBefore(startTime)) {
            return false;
        }
        if (endTime != null && now.isAfter(endTime)) {
            return false;
        }
        return isActive;
    }
} 