package com.ownervoting.controller;

import com.ownervoting.model.entity.AdminUser;
import com.ownervoting.service.AdminUserService;
import com.ownervoting.model.vo.ApiResponse;
import com.ownervoting.model.dto.AdminUserAddDTO;
import com.ownervoting.model.vo.AdminUserVO;
import com.ownervoting.model.dto.OwnerReviewDTO;
import com.ownervoting.service.OwnerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/admin-user")
@Validated
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private OwnerService ownerService;

    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @PostMapping("/add")
    public ApiResponse<AdminUserVO> addAdminUser(@Valid @RequestBody AdminUserAddDTO dto) {
        AdminUser user = new AdminUser();
        user.setUsername(dto.getUsername());
        user.setPasswordHash(dto.getPassword());
        user.setName(dto.getName());
        user.setPhone(dto.getPhone());
        user.setEmail(dto.getEmail());
        user.setRole(AdminUser.Role.valueOf(dto.getRole()));
        // communityId应通过Service查找并set
        AdminUser saved = adminUserService.addAdminUser(user);
        AdminUserVO vo = toVO(saved);
        return ApiResponse.success(vo);
    }

    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteAdminUser(@PathVariable Long id) {
        adminUserService.deleteAdminUser(id);
        return ApiResponse.success(null);
    }

    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @GetMapping("/{id}")
    public ApiResponse<AdminUserVO> getById(@PathVariable Long id) {
        AdminUser user = adminUserService.findById(id);
        if (user == null) return ApiResponse.success(null);
        return ApiResponse.success(toVO(user));
    }

    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @GetMapping("/by-username")
    public ApiResponse<AdminUserVO> getByUsername(@RequestParam String username) {
        AdminUser user = adminUserService.findByUsername(username);
        if (user == null) return ApiResponse.success(null);
        return ApiResponse.success(toVO(user));
    }

    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @GetMapping("/all")
    public ApiResponse<List<AdminUserVO>> getAll() {
        List<AdminUser> list = adminUserService.findAll();
        return ApiResponse.success(list.stream().map(this::toVO).toList());
    }

    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    @PostMapping("/owner-review")
    public ApiResponse<Void> reviewOwnerVerify(@Valid @RequestBody OwnerReviewDTO dto) {
        ownerService.reviewVerifyRequest(dto);
        return ApiResponse.success(null);
    }

    private AdminUserVO toVO(AdminUser u) {
        AdminUserVO vo = new AdminUserVO();
        vo.setId(u.getId());
        vo.setUsername(u.getUsername());
        vo.setName(u.getName());
        vo.setPhone(u.getPhone());
        vo.setEmail(u.getEmail());
        vo.setRole(u.getRole() != null ? u.getRole().name() : null);
        vo.setCommunityId(u.getCommunity() != null ? u.getCommunity().getId() : null);
        vo.setIsActive(u.getIsActive());
        vo.setLastLoginAt(u.getLastLoginAt());
        vo.setCreatedAt(u.getCreatedAt());
        vo.setUpdatedAt(u.getUpdatedAt());
        return vo;
    }
} 