package com.ownervoting.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "system_log")
public class SystemLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private UserType userType; // OWNER, ADMIN

    @Column(length = 100)
    private String operation;

    @Column(length = 50)
    private String ipAddress;

    @Column(name = "content", columnDefinition = "TEXT")
    private String detail;

    @Column(name = "created_time")
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public enum UserType {
        OWNER, ADMIN
    }

    public Long getUserId() {
        return userId;
    }
} 