package com.ownervoting.service;

import com.ownervoting.model.entity.FileUpload;
import java.util.List;

public interface FileUploadService {
    FileUpload addFile(FileUpload fileUpload);
    void deleteFile(Long id);
    FileUpload findById(Long id);
    List<FileUpload> findByOwnerId(Long ownerId);
} 