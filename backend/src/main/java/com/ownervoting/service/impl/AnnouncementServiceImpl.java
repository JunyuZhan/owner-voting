package com.ownervoting.service.impl;

import com.ownervoting.model.entity.Announcement;
import com.ownervoting.repository.AnnouncementRepository;
import com.ownervoting.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Override
    @Transactional
    public Announcement addAnnouncement(Announcement announcement) {
        return announcementRepository.save(announcement);
    }

    @Override
    @Transactional
    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }

    @Override
    public Announcement findById(Long id) {
        return announcementRepository.findById(id).orElse(null);
    }

    @Override
    public List<Announcement> findAll() {
        return announcementRepository.findAll();
    }

    @Override
    public List<Announcement> findByCommunityId(Long communityId) {
        return announcementRepository.findAll().stream()
            .filter(a -> a.getCommunity() != null && a.getCommunity().getId().equals(communityId))
            .toList();
    }

    @Override
    public Page<Announcement> findPage(Pageable pageable) {
        return announcementRepository.findAll(pageable);
    }
} 