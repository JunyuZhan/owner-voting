package com.ownervoting.service;

import com.ownervoting.model.entity.AdminUser;
import java.util.List;

public interface AdminUserService {
    AdminUser addAdminUser(AdminUser adminUser);
    void deleteAdminUser(Long id);
    AdminUser findById(Long id);
    AdminUser findByUsername(String username);
    List<AdminUser> findAll();
} 