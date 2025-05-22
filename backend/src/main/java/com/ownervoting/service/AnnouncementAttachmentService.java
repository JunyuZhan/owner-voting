package com.ownervoting.service;

import com.ownervoting.model.entity.AnnouncementAttachment;
import java.util.List;

public interface AnnouncementAttachmentService {
    AnnouncementAttachment addAttachment(AnnouncementAttachment attachment);
    void deleteAttachment(Long id);
    AnnouncementAttachment findById(Long id);
    List<AnnouncementAttachment> findByAnnouncementId(Long announcementId);
} 