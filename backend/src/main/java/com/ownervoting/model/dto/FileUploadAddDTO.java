package com.ownervoting.model.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;

@Data
public class FileUploadAddDTO {
    @NotNull(message = "业主ID不能为空")
    private Long ownerId;
    @NotBlank(message = "类型不能为空")
    private String type;
    @NotBlank(message = "原文件名不能为空")
    private String originalName;
    @NotBlank(message = "存储路径不能为空")
    private String storagePath;
    private String ocrText;
    private Long fileSize;
    private String fileType;
} 