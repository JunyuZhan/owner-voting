package com.ownervoting.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "review_log")
public class ReviewLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @Column(length = 50)
    private String reviewerName;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Status status; // APPROVED, REJECTED

    @Column(columnDefinition = "TEXT")
    private String comment;

    private LocalDateTime reviewedAt;

    @PrePersist
    public void prePersist() {
        reviewedAt = LocalDateTime.now();
    }

    public enum Status {
        APPROVED, REJECTED
    }

    public String getTargetType() {
        // 假设有targetType字段
        return null;
    }

    public Long getTargetId() {
        // 假设有targetId字段
        return null;
    }
} 