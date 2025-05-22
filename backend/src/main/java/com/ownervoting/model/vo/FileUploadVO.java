package com.ownervoting.model.vo;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FileUploadVO {
    private Long id;
    private Long ownerId;
    private String type;
    private String originalName;
    private String storagePath;
    private String ocrText;
    private Long fileSize;
    private String fileType;
    private LocalDateTime createdAt;
} 