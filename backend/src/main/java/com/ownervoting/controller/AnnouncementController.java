package com.ownervoting.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ownervoting.model.dto.AnnouncementAddDTO;
import com.ownervoting.model.entity.Announcement;
import com.ownervoting.model.vo.AnnouncementVO;
import com.ownervoting.model.vo.ApiResponse;
import com.ownervoting.service.AnnouncementService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/announcement")
@Validated
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','COMMUNITY_ADMIN','OPERATOR')")
    @PostMapping("/add")
    public ApiResponse<AnnouncementVO> addAnnouncement(@Valid @RequestBody AnnouncementAddDTO dto) {
        System.out.println("AnnouncementController.addAnnouncement 被调用");
        System.out.println("当前用户权限: " + org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getAuthorities());
        
        Announcement ann = new Announcement();
        ann.setTitle(dto.getTitle());
        ann.setContent(dto.getContent());
        ann.setType(Announcement.Type.valueOf(dto.getType()));
        ann.setIsPinned(dto.getIsPinned());
        ann.setPublishedAt(dto.getPublishedAt());
        // community和createdBy应通过Service查找并set
        Announcement saved = announcementService.addAnnouncement(ann);
        AnnouncementVO vo = toVO(saved);
        return ApiResponse.success(vo);
    }

    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','COMMUNITY_ADMIN','OPERATOR')")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteAnnouncement(@PathVariable Long id) {
        announcementService.deleteAnnouncement(id);
        return ApiResponse.success(null);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ApiResponse<AnnouncementVO> getById(@PathVariable Long id) {
        Announcement ann = announcementService.findById(id);
        if (ann == null) return ApiResponse.success(null);
        return ApiResponse.success(toVO(ann));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/all")
    public ApiResponse<List<AnnouncementVO>> getAll() {
        List<Announcement> list = announcementService.findAll();
        return ApiResponse.success(list.stream().map(this::toVO).toList());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/page")
    public ApiResponse<Map<String, Object>> getPage(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<Announcement> paged = announcementService.findPage(PageRequest.of(page, size));
        Map<String, Object> result = new HashMap<>();
        result.put("total", paged.getTotalElements());
        result.put("list", paged.getContent().stream().map(this::toVO).toList());
        return ApiResponse.success(result);
    }

    private AnnouncementVO toVO(Announcement ann) {
        AnnouncementVO vo = new AnnouncementVO();
        vo.setId(ann.getId());
        vo.setCommunityId(ann.getCommunity() != null ? ann.getCommunity().getId() : null);
        vo.setTitle(ann.getTitle());
        vo.setContent(ann.getContent());
        vo.setType(ann.getType() != null ? ann.getType().name() : null);
        vo.setIsPinned(ann.getIsPinned());
        vo.setPublishedAt(ann.getPublishedAt());
        // createdBy类型为String，VO为Long，实际应做转换
        vo.setCreatedBy(null);
        return vo;
    }

    /**
     * 获取公开公告（无需登录）
     */
    @GetMapping("/public")
    public ApiResponse<List<AnnouncementVO>> getPublicAnnouncements() {
        try {
            // 只返回已发布的公告，可以限制数量
            List<Announcement> announcements = announcementService.findPublicAnnouncements();
            List<AnnouncementVO> voList = announcements.stream()
                .map(this::toVO)
                .limit(10) // 最多显示10条
                .toList();
            return ApiResponse.success(voList);
        } catch (Exception e) {
            return ApiResponse.error(500, "获取公开公告失败");
        }
    }

    /**
     * 更新公告
     */
    @PutMapping("/update/{id}")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'COMMUNITY_ADMIN', 'OPERATOR')")
    public ApiResponse<AnnouncementVO> updateAnnouncement(@PathVariable Long id, @Valid @RequestBody AnnouncementAddDTO dto) {
        Announcement existing = announcementService.findById(id);
        if (existing == null) {
            return ApiResponse.error(404, "公告不存在");
        }
        
        existing.setTitle(dto.getTitle());
        existing.setContent(dto.getContent());
        existing.setType(Announcement.Type.valueOf(dto.getType()));
        existing.setIsPinned(dto.getIsPinned());
        existing.setPublishedAt(dto.getPublishedAt());
        
        Announcement saved = announcementService.updateAnnouncement(existing);
        return ApiResponse.success(toVO(saved));
    }

    /**
     * 简单的权限测试方法
     */
    @GetMapping("/test-admin")
    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN', 'COMMUNITY_ADMIN', 'OPERATOR')")
    public ApiResponse<String> testAnnouncementAdminAccess() {
        return ApiResponse.success("公告管理权限验证成功！");
    }
} 