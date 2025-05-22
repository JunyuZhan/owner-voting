package com.ownervoting.service;

import com.ownervoting.model.entity.AnnouncementRead;
import java.util.List;

public interface AnnouncementReadService {
    AnnouncementRead addRead(AnnouncementRead read);
    void deleteRead(Long id);
    AnnouncementRead findById(Long id);
    List<AnnouncementRead> findByAnnouncementId(Long announcementId);
    List<AnnouncementRead> findByOwnerId(Long ownerId);
} 