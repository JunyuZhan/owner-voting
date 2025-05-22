package com.ownervoting.controller;

import com.ownervoting.model.entity.AnnouncementRead;
import com.ownervoting.service.AnnouncementReadService;
import com.ownervoting.model.vo.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.ownervoting.service.AnnouncementService;
import com.ownervoting.service.OwnerService;
import com.ownervoting.model.entity.Announcement;
import com.ownervoting.model.entity.Owner;

import java.util.List;

@RestController
@RequestMapping("/api/announcement-read")
public class AnnouncementReadController {

    @Autowired
    private AnnouncementReadService readService;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private OwnerService ownerService;

    @PostMapping("/add")
    public ApiResponse<AnnouncementRead> addRead(@RequestBody AnnouncementRead read) {
        return ApiResponse.success(readService.addRead(read));
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteRead(@PathVariable Long id) {
        readService.deleteRead(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}")
    public ApiResponse<AnnouncementRead> getById(@PathVariable Long id) {
        return ApiResponse.success(readService.findById(id));
    }

    @GetMapping("/by-announcement/{announcementId}")
    public ApiResponse<List<AnnouncementRead>> getByAnnouncementId(@PathVariable Long announcementId) {
        return ApiResponse.success(readService.findByAnnouncementId(announcementId));
    }

    @GetMapping("/by-owner/{ownerId}")
    public ApiResponse<List<AnnouncementRead>> getByOwnerId(@PathVariable Long ownerId) {
        return ApiResponse.success(readService.findByOwnerId(ownerId));
    }

    @PostMapping("/announcements/{id}/read")
    public ApiResponse<Void> markAnnouncementRead(@PathVariable Long id, @RequestParam Long ownerId) {
        AnnouncementRead read = new AnnouncementRead();
        Announcement ann = announcementService.findById(id);
        Owner owner = ownerService.findById(ownerId);
        if (ann == null || owner == null) {
            return ApiResponse.error(404, "公告或用户不存在");
        }
        read.setAnnouncement(ann);
        read.setOwner(owner);
        readService.addRead(read);
        return ApiResponse.success(null);
    }
} 