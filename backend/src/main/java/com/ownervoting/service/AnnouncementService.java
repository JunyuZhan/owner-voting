package com.ownervoting.service;

import com.ownervoting.model.entity.Announcement;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AnnouncementService {
    Announcement addAnnouncement(Announcement announcement);
    Announcement updateAnnouncement(Announcement announcement);
    void deleteAnnouncement(Long id);
    Announcement findById(Long id);
    List<Announcement> findByCommunityId(Long communityId);
    List<Announcement> findAll();
    Page<Announcement> findPage(Pageable pageable);
    List<Announcement> findPublicAnnouncements();
} 