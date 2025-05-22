package com.ownervoting.service.impl;

import com.ownervoting.config.CacheConfig;
import com.ownervoting.exception.NotFoundException;
import com.ownervoting.model.entity.VoteTopic;
import com.ownervoting.repository.VoteTopicRepository;
import com.ownervoting.service.VoteTopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VoteTopicServiceImpl implements VoteTopicService {

    @Autowired
    private VoteTopicRepository voteTopicRepository;

    @Override
    @Cacheable(value = CacheConfig.VOTE_CACHE, key = "'topic:' + #id")
    public VoteTopic findById(Long id) {
        return voteTopicRepository.findById(id).orElse(null);
    }

    @Override
    @Cacheable(value = CacheConfig.VOTE_CACHE, key = "'topics:all'")
    public List<VoteTopic> findAll() {
        return voteTopicRepository.findAll();
    }

    @Override
    @Cacheable(value = CacheConfig.VOTE_CACHE, key = "'topics:active'")
    public List<VoteTopic> findActiveTopics() {
        LocalDateTime now = LocalDateTime.now();
        return voteTopicRepository.findByStatusAndStartTimeBeforeAndEndTimeAfter("PUBLISHED", now, now);
    }

    @Override
    @Cacheable(value = CacheConfig.VOTE_CACHE, key = "'topics:community:' + #communityId")
    public List<VoteTopic> findByCommunityId(Long communityId) {
        return voteTopicRepository.findByCommunityId(communityId);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheConfig.VOTE_CACHE, key = "'topics:all'"),
        @CacheEvict(value = CacheConfig.VOTE_CACHE, key = "'topics:active'"),
        @CacheEvict(value = CacheConfig.VOTE_CACHE, key = "'topics:community:' + #topic.community.id", condition = "#topic.community != null")
    })
    public VoteTopic addTopic(VoteTopic topic) {
        return voteTopicRepository.save(topic);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheConfig.VOTE_CACHE, key = "'topic:' + #topic.id"),
        @CacheEvict(value = CacheConfig.VOTE_CACHE, key = "'topics:all'"),
        @CacheEvict(value = CacheConfig.VOTE_CACHE, key = "'topics:active'"),
        @CacheEvict(value = CacheConfig.VOTE_CACHE, key = "'topics:community:' + #topic.community.id", condition = "#topic.community != null")
    })
    public VoteTopic updateTopic(VoteTopic topic) {
        if (!voteTopicRepository.existsById(topic.getId())) {
            throw NotFoundException.of("投票主题", topic.getId());
        }
        return voteTopicRepository.save(topic);
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheConfig.VOTE_CACHE, key = "'topic:' + #id"),
        @CacheEvict(value = CacheConfig.VOTE_CACHE, key = "'topics:all'"),
        @CacheEvict(value = CacheConfig.VOTE_CACHE, key = "'topics:active'"),
        @CacheEvict(value = CacheConfig.VOTE_CACHE, allEntries = true)
    })
    public void deleteTopic(Long id) {
        VoteTopic topic = voteTopicRepository.findById(id)
                .orElseThrow(() -> NotFoundException.of("投票主题", id));
        voteTopicRepository.delete(topic);
    }

    @Override
    public Page<VoteTopic> findPage(Pageable pageable) {
        return voteTopicRepository.findAll(pageable);
    }
} 