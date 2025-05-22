package com.ownervoting.service.impl;

import com.ownervoting.model.entity.AdminUser;
import com.ownervoting.repository.AdminUserRepository;
import com.ownervoting.service.AdminUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
        return adminUserRepository.findById(id).orElse(null);
    }

    @Override
    public AdminUser findByUsername(String username) {
        return adminUserRepository.findAll().stream()
                .filter(a -> a.getUsername().equals(username))
                .findFirst().orElse(null);
    }

    @Override
    public List<AdminUser> findAll() {
        return adminUserRepository.findAll();
    }
} 