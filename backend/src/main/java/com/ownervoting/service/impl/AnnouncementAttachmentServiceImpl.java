package com.ownervoting.service.impl;

import com.ownervoting.model.entity.AnnouncementAttachment;
import com.ownervoting.repository.AnnouncementAttachmentRepository;
import com.ownervoting.service.AnnouncementAttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnnouncementAttachmentServiceImpl implements AnnouncementAttachmentService {

    @Autowired
    private AnnouncementAttachmentRepository attachmentRepository;

    @Override
    @Transactional
    public AnnouncementAttachment addAttachment(AnnouncementAttachment attachment) {
        return attachmentRepository.save(attachment);
    }

    @Override
    @Transactional
    public void deleteAttachment(Long id) {
        attachmentRepository.deleteById(id);
    }

    @Override
    public AnnouncementAttachment findById(Long id) {
        return attachmentRepository.findById(id).orElse(null);
    }

    @Override
    public List<AnnouncementAttachment> findByAnnouncementId(Long announcementId) {
        return attachmentRepository.findAll().stream()
                .filter(a -> a.getAnnouncement() != null && a.getAnnouncement().getId().equals(announcementId))
                .toList();
    }
} 