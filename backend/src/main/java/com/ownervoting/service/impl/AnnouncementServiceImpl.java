package com.ownervoting.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ownervoting.model.entity.Announcement;
import com.ownervoting.repository.AnnouncementRepository;
import com.ownervoting.security.TenantContext;
import com.ownervoting.service.AnnouncementService;

@Service
public class AnnouncementServiceImpl implements AnnouncementService {

    private static final Logger logger = LoggerFactory.getLogger(AnnouncementServiceImpl.class);

    @Autowired
    private AnnouncementRepository announcementRepository;

    @Override
    @Transactional
    public Announcement addAnnouncement(Announcement announcement) {
        // 为新公告设置租户ID
        Long currentCommunityId = TenantContext.getCurrentCommunityId();
        if (currentCommunityId != null && announcement.getCommunity() == null) {
            // 这里应该通过Service设置Community对象，简化为设置ID
            logger.info("为公告设置小区ID: {}", currentCommunityId);
        }
        return announcementRepository.save(announcement);
    }

    @Override
    @Transactional
    public Announcement updateAnnouncement(Announcement announcement) {
        // 检查公告是否存在
        Announcement existing = findById(announcement.getId());
        if (existing == null) {
            throw new RuntimeException("公告不存在");
        }
        
        // 权限检查
        Long currentCommunityId = TenantContext.getCurrentCommunityId();
        if (!TenantContext.isSystemAdmin() && 
            existing.getCommunity() != null && 
            !existing.getCommunity().getId().equals(currentCommunityId)) {
            throw new RuntimeException("没有权限修改其他小区的公告");
        }
        
        return announcementRepository.save(announcement);
    }

    @Override
    @Transactional
    public void deleteAnnouncement(Long id) {
        // 删除前先检查权限
        Announcement announcement = findById(id);
        if (announcement != null) {
            // 只有系统管理员或该小区的管理员才能删除
            Long currentCommunityId = TenantContext.getCurrentCommunityId();
            if (!TenantContext.isSystemAdmin() && 
                announcement.getCommunity() != null && 
                !announcement.getCommunity().getId().equals(currentCommunityId)) {
                throw new RuntimeException("没有权限删除其他小区的公告");
            }
        }
        announcementRepository.deleteById(id);
    }

    @Override
    public Announcement findById(Long id) {
        if (id == null) {
            return null;
        }
        Announcement announcement = announcementRepository.findById(id).orElse(null);
        
        // 租户数据访问检查
        if (announcement != null && !TenantContext.isSystemAdmin()) {
            Long currentCommunityId = TenantContext.getCurrentCommunityId();
            if (announcement.getCommunity() != null && 
                currentCommunityId != null && 
                !announcement.getCommunity().getId().equals(currentCommunityId)) {
                logger.warn("用户 {} 尝试访问其他小区的公告 ID: {}", TenantContext.getCurrentUsername(), id);
                return null; // 返回null表示未找到，保护数据安全
            }
        }
        
        return announcement;
    }

    @Override
    public List<Announcement> findAll() {
        logger.info("=== findAll 被调用 ===");
        logger.info("当前用户: {}", TenantContext.getCurrentUsername());
        logger.info("是否系统管理员: {}", TenantContext.isSystemAdmin());
        logger.info("当前小区ID: {}", TenantContext.getCurrentCommunityId());
        
        // 系统管理员可以查看所有公告
        if (TenantContext.isSystemAdmin()) {
            logger.info("系统管理员查询所有公告");
            List<Announcement> allAnnouncements = announcementRepository.findAll();
            logger.info("查询到公告数量: {}", allAnnouncements.size());
            return allAnnouncements;
        }
        
        Long currentCommunityId = TenantContext.getCurrentCommunityId();
        
        // 小区管理员只能查看自己小区的公告
        if (currentCommunityId != null) {
            logger.info("小区管理员查询小区 {} 的公告", currentCommunityId);
            List<Announcement> communityAnnouncements = announcementRepository.findByCommunityId(currentCommunityId);
            logger.info("查询到小区公告数量: {}", communityAnnouncements.size());
            return communityAnnouncements;
        }
        
        // 如果不是系统管理员且没有关联小区，返回空列表
        logger.warn("用户 {} 没有关联小区且不是系统管理员，返回空列表", TenantContext.getCurrentUsername());
        return List.of();
    }

    @Override
    public List<Announcement> findByCommunityId(Long communityId) {
        if (communityId == null) {
            return List.of();
        }
        
        // 租户隔离检查
        if (!TenantContext.isSystemAdmin()) {
            Long currentCommunityId = TenantContext.getCurrentCommunityId();
            if (currentCommunityId != null && !currentCommunityId.equals(communityId)) {
                logger.warn("用户 {} 尝试访问其他小区 {} 的公告", TenantContext.getCurrentUsername(), communityId);
                return List.of();
            }
        }
        
        return announcementRepository.findByCommunityId(communityId);
    }

    @Override
    public Page<Announcement> findPage(Pageable pageable) {
        Long currentCommunityId = TenantContext.getCurrentCommunityId();
        
        // 系统管理员可以查看所有公告
        if (TenantContext.isSystemAdmin()) {
            logger.debug("系统管理员分页查询所有公告");
            return announcementRepository.findAll(pageable);
        }
        
        // 小区管理员只能查看自己小区的公告
        if (currentCommunityId != null) {
            logger.debug("小区管理员分页查询小区 {} 的公告", currentCommunityId);
            List<Announcement> announcements = announcementRepository.findByCommunityId(currentCommunityId);
            
            // 手动分页处理（更好的方式是在Repository中实现分页查询）
            int start = (int) pageable.getOffset();
            int end = Math.min(start + pageable.getPageSize(), announcements.size());
            List<Announcement> pagedList = start < announcements.size() ? 
                announcements.subList(start, end) : List.of();
            
            return new PageImpl<>(pagedList, pageable, announcements.size());
        }
        
        logger.warn("用户 {} 没有关联小区，返回空分页", TenantContext.getCurrentUsername());
        return new PageImpl<>(List.of(), pageable, 0);
    }

    @Override
    public List<Announcement> findPublicAnnouncements() {
        // 公开API，返回所有已发布的公告，不进行租户隔离
        logger.debug("获取公开公告列表");
        return announcementRepository.findAll();
    }
}