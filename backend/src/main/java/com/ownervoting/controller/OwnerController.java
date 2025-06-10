package com.ownervoting.controller;

import com.ownervoting.model.entity.Owner;
import com.ownervoting.service.OwnerService;
import com.ownervoting.model.vo.ApiResponse;
import com.ownervoting.model.dto.OwnerRegisterDTO;
import com.ownervoting.model.vo.OwnerVO;
import com.ownervoting.model.dto.OwnerVerifyDTO;
import com.ownervoting.model.dto.OwnerVerificationReviewDTO;
import com.ownervoting.model.vo.OwnerVerifyStatusVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import org.springframework.web.multipart.MultipartFile;
import com.ownervoting.service.FileUploadService;
import com.ownervoting.model.entity.FileUpload;
import java.io.File;
import java.io.IOException;
import java.util.UUID;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/owner")
@Validated
public class OwnerController {

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/register")
    public ApiResponse<OwnerVO> register(@Valid @RequestBody OwnerRegisterDTO dto) {
        Owner owner = new Owner();
        owner.setPhone(dto.getPhone());
        owner.setName(dto.getName());
        owner.setIdCard(dto.getIdCard());
        owner.setPasswordHash(dto.getPassword());
        Owner saved = ownerService.registerOwner(owner);
        OwnerVO vo = new OwnerVO();
        vo.setId(saved.getId());
        vo.setPhone(saved.getPhone());
        vo.setName(saved.getName());
        vo.setIsVerified(saved.getIsVerified());
        vo.setStatus(saved.getStatus());
        return ApiResponse.success(vo);
    }

    @GetMapping("/by-phone")
    public ApiResponse<OwnerVO> getByPhone(@RequestParam String phone) {
        Owner owner = ownerService.findByPhone(phone);
        if (owner == null) return ApiResponse.success(null);
        OwnerVO vo = new OwnerVO();
        vo.setId(owner.getId());
        vo.setPhone(owner.getPhone());
        vo.setName(owner.getName());
        vo.setIsVerified(owner.getIsVerified());
        vo.setStatus(owner.getStatus());
        return ApiResponse.success(vo);
    }

    @PostMapping("/verify")
    public ApiResponse<Void> submitVerifyRequest(@Valid @RequestBody OwnerVerifyDTO dto) {
        ownerService.submitVerifyRequest(dto);
        return ApiResponse.success(null);
    }

    @GetMapping("/verify-status")
    public ApiResponse<OwnerVerifyStatusVO> getVerifyStatus(@RequestParam Long ownerId) {
        OwnerVerifyStatusVO vo = ownerService.getVerifyStatus(ownerId);
        return ApiResponse.success(vo);
    }

    @PostMapping("/verification/upload")
    public ApiResponse<String> uploadVerificationFile(@RequestParam("file") MultipartFile file, @RequestParam("ownerId") Long ownerId, @RequestParam("type") String type) {
        if (file.isEmpty()) {
            return ApiResponse.error(400, "文件不能为空");
        }
        try {
            // 保存文件到本地（实际可用MinIO等对象存储）
            String uploadDir = "uploads/verification/";
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();
            String ext = file.getOriginalFilename() != null ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.')) : "";
            String filename = UUID.randomUUID() + ext;
            File dest = new File(dir, filename);
            file.transferTo(dest);
            // 创建文件上传记录
            FileUpload upload = new FileUpload();
            Owner owner = new Owner(); owner.setId(ownerId); upload.setOwner(owner);
            upload.setType(FileUpload.FileType.valueOf(type));
            upload.setOriginalName(file.getOriginalFilename());
            upload.setStoragePath(dest.getAbsolutePath());
            upload.setFileSize(file.getSize());
            upload.setContentType(file.getContentType());
            fileUploadService.addFile(upload);
            return ApiResponse.success(dest.getAbsolutePath());
        } catch (IOException e) {
            return ApiResponse.error(500, "文件上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/verification/submit")
    public ApiResponse<Void> submitVerification(@Valid @RequestBody OwnerVerifyDTO dto) {
        ownerService.submitVerifyRequest(dto);
        return ApiResponse.success(null);
    }

    @GetMapping("/verification/status")
    public ApiResponse<OwnerVerifyStatusVO> getVerificationStatus(@RequestParam Long ownerId) {
        OwnerVerifyStatusVO vo = ownerService.getVerifyStatus(ownerId);
        return ApiResponse.success(vo);
    }

    // ==================== 管理员端点 ====================

    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @PostMapping("/admin/add")
    public ApiResponse<OwnerVO> addOwner(@Valid @RequestBody com.ownervoting.model.dto.OwnerAddDTO dto) {
        Owner owner = ownerService.addOwner(dto);
        OwnerVO vo = toVO(owner);
        return ApiResponse.success(vo);
    }

    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'COMMUNITY_ADMIN', 'OPERATOR')")
    @GetMapping("/list")
    public ApiResponse<List<OwnerVO>> getOwnerList() {
        List<Owner> owners = ownerService.findAll();
        List<OwnerVO> voList = owners.stream().map(this::toVO).toList();
        return ApiResponse.success(voList);
    }

    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'COMMUNITY_ADMIN', 'OPERATOR')")
    @PostMapping("/verification/review")
    public ApiResponse<Void> reviewOwnerVerification(@Valid @RequestBody OwnerVerificationReviewDTO dto) {
        ownerService.reviewVerification(dto.getOwnerId(), dto.getStatus(), dto.getReviewComment());
        return ApiResponse.success(null);
    }

    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteOwner(@PathVariable Long id) {
        ownerService.deleteOwner(id);
        return ApiResponse.success(null);
    }

    private OwnerVO toVO(Owner owner) {
        OwnerVO vo = new OwnerVO();
        vo.setId(owner.getId());
        vo.setPhone(owner.getPhone());
        vo.setName(owner.getName());
        vo.setIsVerified(owner.getIsVerified());
        vo.setStatus(owner.getStatus());
        vo.setCreatedAt(owner.getCreatedAt());
        vo.setUpdatedAt(owner.getUpdatedAt());
        return vo;
    }
}