package com.ownervoting.service;

import java.util.List;

import com.ownervoting.model.entity.AdminUser;

public interface AdminUserService {
    AdminUser addAdminUser(AdminUser adminUser);
    void deleteAdminUser(Long id);
    AdminUser findById(Long id);
    AdminUser findByUsername(String username);
    AdminUser findByUsernameWithCommunity(String username);
    List<AdminUser> findAll();
} 