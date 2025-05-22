package com.ownervoting.controller;

import com.ownervoting.model.entity.AnnouncementAttachment;
import com.ownervoting.service.AnnouncementAttachmentService;
import com.ownervoting.model.vo.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcement-attachment")
public class AnnouncementAttachmentController {

    @Autowired
    private AnnouncementAttachmentService attachmentService;

    @PostMapping("/add")
    public ApiResponse<AnnouncementAttachment> addAttachment(@RequestBody AnnouncementAttachment attachment) {
        return ApiResponse.success(attachmentService.addAttachment(attachment));
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteAttachment(@PathVariable Long id) {
        attachmentService.deleteAttachment(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}")
    public ApiResponse<AnnouncementAttachment> getById(@PathVariable Long id) {
        return ApiResponse.success(attachmentService.findById(id));
    }

    @GetMapping("/by-announcement/{announcementId}")
    public ApiResponse<List<AnnouncementAttachment>> getByAnnouncementId(@PathVariable Long announcementId) {
        return ApiResponse.success(attachmentService.findByAnnouncementId(announcementId));
    }
} 