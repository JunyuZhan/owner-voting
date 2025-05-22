package com.ownervoting.service.impl;

import com.ownervoting.model.entity.AnnouncementRead;
import com.ownervoting.repository.AnnouncementReadRepository;
import com.ownervoting.service.AnnouncementReadService;
import com.ownervoting.service.SystemLogService;
import com.ownervoting.model.entity.SystemLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AnnouncementReadServiceImpl implements AnnouncementReadService {

    @Autowired
    private AnnouncementReadRepository readRepository;

    @Autowired
    private SystemLogService systemLogService;

    @Override
    @Transactional
    public AnnouncementRead addRead(AnnouncementRead read) {
        AnnouncementRead saved = readRepository.save(read);
        // 日志埋点
        SystemLog log = new SystemLog();
        log.setUserId(read.getOwner() != null ? read.getOwner().getId() : null);
        log.setUserType(SystemLog.UserType.OWNER);
        log.setOperation("公告已读");
        log.setDetail("用户已读公告，公告ID：" + (read.getAnnouncement() != null ? read.getAnnouncement().getId() : null));
        systemLogService.addLog(log);
        return saved;
    }

    @Override
    @Transactional
    public void deleteRead(Long id) {
        readRepository.deleteById(id);
    }

    @Override
    public AnnouncementRead findById(Long id) {
        return readRepository.findById(id).orElse(null);
    }

    @Override
    public List<AnnouncementRead> findByAnnouncementId(Long announcementId) {
        return readRepository.findAll().stream()
                .filter(r -> r.getAnnouncement() != null && r.getAnnouncement().getId().equals(announcementId))
                .toList();
    }

    @Override
    public List<AnnouncementRead> findByOwnerId(Long ownerId) {
        return readRepository.findAll().stream()
                .filter(r -> r.getOwner() != null && r.getOwner().getId().equals(ownerId))
                .toList();
    }
} 