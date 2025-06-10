package com.ownervoting.model.dto;

import com.ownervoting.model.entity.AdminUser;
import com.ownervoting.model.entity.Community;

import lombok.Data;

@Data
public class AdminUserDTO {
    private Long id;
    private String username;
    private String name;
    private String phone;
    private String email;
    private String role;
    private Boolean enabled;
    private CommunityDTO community;
    
    // 静态方法将AdminUser转换为DTO
    public static AdminUserDTO fromEntity(AdminUser admin) {
        AdminUserDTO dto = new AdminUserDTO();
        dto.setId(admin.getId());
        dto.setUsername(admin.getUsername());
        dto.setName(admin.getName());
        dto.setPhone(admin.getPhone());
        dto.setEmail(admin.getEmail());
        dto.setRole(admin.getRole() != null ? admin.getRole().name() : null);
        dto.setEnabled(admin.getEnabled());
        
        // 处理community - 避免懒加载问题
        if (admin.getCommunity() != null) {
            try {
                Community community = admin.getCommunity();
                CommunityDTO communityDTO = new CommunityDTO();
                communityDTO.setId(community.getId());
                communityDTO.setName(community.getName());
                communityDTO.setAddress(community.getAddress());
                communityDTO.setDescription(community.getDescription());
                dto.setCommunity(communityDTO);
            } catch (Exception e) {
                // 懒加载失败时，设置为null
                dto.setCommunity(null);
            }
        }
        
        return dto;
    }
    
    @Data
    public static class CommunityDTO {
        private Long id;
        private String name;
        private String address;
        private String description;
    }
} 