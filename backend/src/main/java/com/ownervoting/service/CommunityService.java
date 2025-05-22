package com.ownervoting.service;

import com.ownervoting.model.entity.Community;
import java.util.List;

public interface CommunityService {
    Community addCommunity(Community community);
    void deleteCommunity(Long id);
    Community findById(Long id);
    List<Community> findAll();
} 