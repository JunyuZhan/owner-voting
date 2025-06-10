package com.ownervoting.controller;

import com.ownervoting.exception.BusinessException;
import com.ownervoting.exception.NotFoundException;
import com.ownervoting.model.entity.FileUpload;
import com.ownervoting.model.entity.Owner;
import com.ownervoting.model.vo.ApiResponse;
import com.ownervoting.service.FileUploadService;
import com.ownervoting.service.OwnerService;
import com.ownervoting.util.FileSecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@RestController
public class FileUploadController {

    @Value("${file.upload.dir:uploads/}")
    private String uploadDir;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private OwnerService ownerService;

    @PostMapping("/upload/image")
    public ApiResponse<?> uploadImage(
            @RequestParam("file") MultipartFile file, 
            @RequestParam(value = "ownerId", required = false) Long ownerId, 
            @RequestParam(value = "type", required = false, defaultValue = "OTHER") String type) {
        
        try {
            // 验证图片文件
            FileSecurityUtils.validateImageFile(file);
            
            // 保存文件并创建记录
            return saveFile(file, ownerId, type);
        } catch (IOException e) {
            throw new BusinessException(500, "文件上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/upload/document")
    public ApiResponse<?> uploadDocument(
            @RequestParam("file") MultipartFile file, 
            @RequestParam(value = "ownerId", required = false) Long ownerId, 
            @RequestParam(value = "type", required = false, defaultValue = "OTHER") String type) {
        
        try {
            // 验证文档文件
            FileSecurityUtils.validateDocumentFile(file);
            
            // 保存文件并创建记录
            return saveFile(file, ownerId, type);
        } catch (IOException e) {
            throw new BusinessException(500, "文件上传失败: " + e.getMessage());
        }
    }
    
    private ApiResponse<?> saveFile(MultipartFile file, Long ownerId, String type) throws IOException {
        // 创建年月日目录结构
        String dateFolder = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String fullUploadDir = uploadDir + dateFolder;
        
        // 创建目录
        Path uploadPath = Paths.get(fullUploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // 生成安全的文件名
        String originalFilename = FileSecurityUtils.getSafeFilename(file.getOriginalFilename());
        String extension = originalFilename.contains(".") ? 
                originalFilename.substring(originalFilename.lastIndexOf('.')) : "";
        String newFilename = UUID.randomUUID().toString() + extension;
        
        // 保存文件
        Path filePath = uploadPath.resolve(newFilename);
        Files.copy(file.getInputStream(), filePath);
        
        // 创建文件上传记录
        FileUpload upload = new FileUpload();
        
        // 设置关联的业主
        if (ownerId != null) {
            Owner owner = ownerService.findById(ownerId);
            if (owner == null) {
                throw NotFoundException.of("业主", ownerId);
            }
            upload.setOwner(owner);
        }
        
        // 设置文件类型
        try { 
            upload.setType(FileUpload.FileType.valueOf(type)); 
        } catch (Exception e) {
            upload.setType(FileUpload.FileType.OTHER);
        }
        
        upload.setOriginalName(originalFilename);
        upload.setStoragePath(filePath.toString());
        upload.setFileSize(file.getSize());
        upload.setContentType(file.getContentType());
        
        // 保存记录
        fileUploadService.addFile(upload);
        
        return ApiResponse.success(upload.getId());
    }

    @GetMapping("/files/{id}")
    public ApiResponse<?> getFileInfo(@PathVariable Long id) {
        FileUpload file = fileUploadService.findById(id);
        if (file == null) {
            throw NotFoundException.of("文件", id);
        }
        return ApiResponse.success(file);
    }
    
    @DeleteMapping("/files/{id}")
    public ApiResponse<?> deleteFile(@PathVariable Long id) {
        FileUpload file = fileUploadService.findById(id);
        if (file == null) {
            throw NotFoundException.of("文件", id);
        }
        
        // 删除文件
        try {
            Files.deleteIfExists(Paths.get(file.getStoragePath()));
        } catch (IOException e) {
            throw new BusinessException("文件删除失败: " + e.getMessage());
        }
        
        // 删除记录
        fileUploadService.deleteFile(id);
        return ApiResponse.success(null);
    }
}