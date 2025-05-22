package com.ownervoting.util;

import com.ownervoting.exception.BusinessException;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 文件安全工具类
 */
public class FileSecurityUtils {

    private static final Set<String> ALLOWED_IMAGE_TYPES = new HashSet<>(Arrays.asList(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/bmp"));
    
    private static final Set<String> ALLOWED_DOCUMENT_TYPES = new HashSet<>(Arrays.asList(
            "application/pdf", "application/msword", 
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
    
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024; // 5MB
    private static final long MAX_DOCUMENT_SIZE = 10 * 1024 * 1024; // 10MB
    
    /**
     * 验证图片文件类型和大小
     * @param file 要验证的文件
     */
    public static void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件为空");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType)) {
            throw new BusinessException("不支持的图片类型，仅支持JPG/PNG/GIF/BMP格式");
        }
        
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new BusinessException("图片大小超过限制，最大支持5MB");
        }
    }
    
    /**
     * 验证文档文件类型和大小
     * @param file 要验证的文件
     */
    public static void validateDocumentFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件为空");
        }
        
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_DOCUMENT_TYPES.contains(contentType)) {
            throw new BusinessException("不支持的文档类型，仅支持PDF/DOC/DOCX/XLS/XLSX格式");
        }
        
        if (file.getSize() > MAX_DOCUMENT_SIZE) {
            throw new BusinessException("文档大小超过限制，最大支持10MB");
        }
    }
    
    /**
     * 获取安全的文件名
     * @param originalFilename 原始文件名
     * @return 安全处理后的文件名
     */
    public static String getSafeFilename(String originalFilename) {
        if (originalFilename == null) {
            return "unnamed";
        }
        
        // 移除路径信息
        String filename = originalFilename.replaceAll(".*[\\\\/]", "");
        
        // 移除特殊字符，只保留字母、数字、点、下划线和连字符
        filename = filename.replaceAll("[^a-zA-Z0-9.\\-_]", "_");
        
        return filename;
    }
} 