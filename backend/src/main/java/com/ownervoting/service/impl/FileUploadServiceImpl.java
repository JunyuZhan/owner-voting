package com.ownervoting.service.impl;

import com.ownervoting.model.entity.FileUpload;
import com.ownervoting.repository.FileUploadRepository;
import com.ownervoting.service.FileUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    private FileUploadRepository fileUploadRepository;

    @Override
    @Transactional
    public FileUpload addFile(FileUpload fileUpload) {
        return fileUploadRepository.save(fileUpload);
    }

    @Override
    @Transactional
    public void deleteFile(Long id) {
        fileUploadRepository.deleteById(id);
    }

    @Override
    public FileUpload findById(Long id) {
        return fileUploadRepository.findById(id).orElse(null);
    }

    @Override
    public List<FileUpload> findByOwnerId(Long ownerId) {
        return fileUploadRepository.findAll().stream()
                .filter(f -> f.getOwnerId() != null && f.getOwnerId().equals(ownerId))
                .toList();
    }
} 