package com.ownervoting.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "announcement_attachment")
public class AnnouncementAttachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "announcement_id")
    private Announcement announcement;

    @Column(length = 255)
    private String originalName;

    @Column(columnDefinition = "TEXT")
    private String storagePath;

    private Long fileSize;

    @Column(length = 50)
    private String fileType;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public Announcement getAnnouncement() {
        return announcement;
    }
} 