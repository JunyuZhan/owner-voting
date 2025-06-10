package com.ownervoting.service;

import com.ownervoting.model.entity.ProxyVote;
import com.ownervoting.model.dto.ProxyVoteDTO;

import java.util.List;

public interface ProxyVoteService {
    
    /**
     * 管理员代理投票
     */
    ProxyVote createProxyVote(ProxyVoteDTO dto);
    
    /**
     * 查找投票议题的代理投票记录
     */
    List<ProxyVote> findByTopicId(Long topicId);
    
    /**
     * 查找业主的代理投票记录
     */
    List<ProxyVote> findByVoterId(Long voterId);
    
    /**
     * 检查是否已代理投票
     */
    boolean hasProxyVoted(Long topicId, Long voterId, Long houseId);
    
    /**
     * 删除代理投票记录
     */
    void deleteProxyVote(Long id);
} 