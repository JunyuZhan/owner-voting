package com.ownervoting.service.impl;

import com.ownervoting.model.entity.FileUpload;
import com.ownervoting.repository.FileUploadRepository;
import com.ownervoting.service.FileUploadService;
import com.ownervoting.service.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class FileUploadServiceImpl implements FileUploadService {
    
    private static final Logger logger = LoggerFactory.getLogger(FileUploadServiceImpl.class);

    @Autowired
    private FileUploadRepository fileUploadRepository;
    
    @Autowired
    private OcrService ocrService;

    @Override
    @Transactional
    public FileUpload addFile(FileUpload fileUpload) {
        return fileUploadRepository.save(fileUpload);
    }
    
    /**
     * 上传文件并进行OCR识别
     * @param file 上传的文件
     * @param fileType 文件类型（ID_CARD_FRONT, ID_CARD_BACK, HOUSE_CERT等）
     * @param ownerId 业主ID
     * @return 文件上传记录
     */
    @Transactional
    public FileUpload uploadWithOcr(MultipartFile file, String fileType, Long ownerId) {
        FileUpload fileUpload = new FileUpload();
        fileUpload.setFileName(file.getOriginalFilename());
        fileUpload.setFileSize(file.getSize());
        fileUpload.setContentType(file.getContentType());
        fileUpload.setOwnerId(ownerId);
        
        // 根据文件类型进行OCR识别
        try {
            OcrService.OcrResult ocrResult = null;
            
            switch (fileType.toUpperCase()) {
                case "ID_CARD_FRONT":
                    ocrResult = ocrService.recognizeIdCard(file, false);
                    break;
                case "ID_CARD_BACK":
                    ocrResult = ocrService.recognizeIdCard(file, true);
                    break;
                case "HOUSE_CERT":
                    ocrResult = ocrService.recognizeHouseCert(file);
                    break;
                default:
                    ocrResult = ocrService.recognizeText(file);
                    break;
            }
            
            if (ocrResult != null && ocrResult.isSuccess()) {
                fileUpload.setOcrText(ocrResult.getText());
                logger.info("文件OCR识别成功: {}", file.getOriginalFilename());
            } else {
                logger.warn("文件OCR识别失败: {}, 错误: {}", 
                    file.getOriginalFilename(), 
                    ocrResult != null ? ocrResult.getError() : "未知错误");
            }
            
        } catch (Exception e) {
            logger.error("OCR识别过程中发生异常", e);
        }
        
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