package com.ownervoting.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ownervoting.model.entity.AdminUser;
import com.ownervoting.repository.AdminUserRepository;
import com.ownervoting.service.AdminUserService;

@Service
public class AdminUserServiceImpl implements AdminUserService {

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Override
    @Transactional
    public AdminUser addAdminUser(AdminUser adminUser) {
        return adminUserRepository.save(adminUser);
    }

    @Override
    @Transactional
    public void deleteAdminUser(Long id) {
        adminUserRepository.deleteById(id);
    }

    @Override
    public AdminUser findById(Long id) {
        if (id == null) {
            return null;
        }
        return adminUserRepository.findById(id).orElse(null);
    }

    @Override
    public AdminUser findByUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        return adminUserRepository.findByUsername(username).orElse(null);
    }

    @Override
    public AdminUser findByUsernameWithCommunity(String username) {
        if (username == null || username.trim().isEmpty()) {
            return null;
        }
        AdminUser admin = adminUserRepository.findByUsername(username).orElse(null);
        if (admin != null && admin.getCommunity() != null) {
            // 触发懒加载
            admin.getCommunity().getName();
        }
        return admin;
    }

    @Override
    public List<AdminUser> findAll() {
        return adminUserRepository.findAll();
    }
}