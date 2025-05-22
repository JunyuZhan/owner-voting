package com.ownervoting.service.impl;

import com.ownervoting.model.entity.Community;
import com.ownervoting.repository.CommunityRepository;
import com.ownervoting.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CommunityServiceImpl implements CommunityService {

    @Autowired
    private CommunityRepository communityRepository;

    @Override
    @Transactional
    public Community addCommunity(Community community) {
        return communityRepository.save(community);
    }

    @Override
    @Transactional
    public void deleteCommunity(Long id) {
        communityRepository.deleteById(id);
    }

    @Override
    public Community findById(Long id) {
        return communityRepository.findById(id).orElse(null);
    }

    @Override
    public List<Community> findAll() {
        return communityRepository.findAll();
    }
} 