package com.ownervoting.service;

import com.ownervoting.model.entity.VoteTopic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VoteTopicService {
    /**
     * 根据ID查找投票主题
     */
    VoteTopic findById(Long id);
    
    /**
     * 查找所有投票主题
     */
    List<VoteTopic> findAll();
    
    /**
     * 查找当前有效的投票主题
     */
    List<VoteTopic> findActiveTopics();
    
    /**
     * 根据小区ID查询投票主题
     */
    List<VoteTopic> findByCommunityId(Long communityId);
    
    /**
     * 分页查询投票主题
     */
    Page<VoteTopic> findPage(Pageable pageable);
    
    /**
     * 添加投票主题
     */
    VoteTopic addTopic(VoteTopic topic);
    
    /**
     * 更新投票主题
     */
    VoteTopic updateTopic(VoteTopic topic);
    
    /**
     * 删除投票主题
     */
    void deleteTopic(Long id);
} 