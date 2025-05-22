package com.ownervoting.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "file_upload")
public class FileUpload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private Owner owner;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private FileType type; // ID_CARD, HOUSE_CERT, OTHER

    @Column(length = 255)
    private String originalName;

    @Column(columnDefinition = "TEXT")
    private String storagePath;

    @Column(columnDefinition = "TEXT")
    private String ocrText;

    private LocalDateTime createdAt;

    @Column
    private Long fileSize;

    @Column(length = 50)
    private String fileType;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    public enum FileType {
        ID_CARD, HOUSE_CERT, OTHER
    }

    public Long getOwnerId() {
        return owner != null ? owner.getId() : null;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
}