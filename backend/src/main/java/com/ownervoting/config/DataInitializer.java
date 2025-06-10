package com.ownervoting.config;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.ownervoting.model.entity.AdminUser;
import com.ownervoting.model.entity.Community;
import com.ownervoting.repository.AdminUserRepository;
import com.ownervoting.repository.CommunityRepository;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private AdminUserRepository adminUserRepository;

    @Override
    public void run(String... args) throws Exception {
        // 检查是否已有数据
        if (adminUserRepository.count() > 0) {
            return;
        }

        // 创建示例社区
        Community community = new Community();
        community.setName("示范小区");
        community.setAddress("北京市海淀区中关村大街1号");
        community.setDescription("这是一个示范小区，用于系统演示");
        community.setCreatedAt(LocalDateTime.now());
        community = communityRepository.save(community);

        // 创建第二个示例社区
        Community community2 = new Community();
        community2.setName("阳光花园");
        community2.setAddress("北京市朝阳区建外大街2号");
        community2.setDescription("另一个示例小区，用于测试租户隔离");
        community2.setCreatedAt(LocalDateTime.now());
        community2 = communityRepository.save(community2);

        // 创建系统管理员账号（不绑定小区）
        AdminUser systemAdmin = new AdminUser();
        systemAdmin.setUsername("admin");
        systemAdmin.setPasswordHash("123456");
        systemAdmin.setName("系统管理员");
        systemAdmin.setRole(AdminUser.Role.SYSTEM_ADMIN);
        systemAdmin.setEnabled(true);
        systemAdmin.setCreatedAt(LocalDateTime.now());
        systemAdmin.setUpdatedAt(LocalDateTime.now());
        adminUserRepository.save(systemAdmin);

        // 创建小区管理员账号（绑定到第一个小区）
        AdminUser communityAdmin = new AdminUser();
        communityAdmin.setUsername("community1");
        communityAdmin.setPasswordHash("123456");
        communityAdmin.setName("示范小区管理员");
        communityAdmin.setRole(AdminUser.Role.COMMUNITY_ADMIN);
        communityAdmin.setCommunity(community);  // 绑定到示范小区
        communityAdmin.setEnabled(true);
        communityAdmin.setCreatedAt(LocalDateTime.now());
        communityAdmin.setUpdatedAt(LocalDateTime.now());
        adminUserRepository.save(communityAdmin);

        // 创建第二个小区管理员账号（绑定到第二个小区）
        AdminUser communityAdmin2 = new AdminUser();
        communityAdmin2.setUsername("community2");
        communityAdmin2.setPasswordHash("123456");
        communityAdmin2.setName("阳光花园管理员");
        communityAdmin2.setRole(AdminUser.Role.COMMUNITY_ADMIN);
        communityAdmin2.setCommunity(community2);  // 绑定到阳光花园
        communityAdmin2.setEnabled(true);
        communityAdmin2.setCreatedAt(LocalDateTime.now());
        communityAdmin2.setUpdatedAt(LocalDateTime.now());
        adminUserRepository.save(communityAdmin2);

        System.out.println("数据初始化完成：");
        System.out.println("- 系统管理员账号: admin/123456");
        System.out.println("- 示范小区管理员: community1/123456");
        System.out.println("- 阳光花园管理员: community2/123456");
    }
}