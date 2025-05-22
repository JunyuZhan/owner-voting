package com.ownervoting.controller;

import com.ownervoting.model.entity.Announcement;
import com.ownervoting.service.AnnouncementService;
import com.ownervoting.model.vo.ApiResponse;
import com.ownervoting.model.dto.AnnouncementAddDTO;
import com.ownervoting.model.vo.AnnouncementVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.Map;
import java.util.HashMap;

import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/announcement")
@Validated
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','COMMUNITY_ADMIN','OPERATOR')")
    @PostMapping("/add")
    public ApiResponse<AnnouncementVO> addAnnouncement(@Valid @RequestBody AnnouncementAddDTO dto) {
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
} 