package com.ownervoting.service;

import com.ownervoting.model.entity.VoteRecord;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface VoteRecordService {
    /**
     * 添加投票记录
     */
    VoteRecord addVoteRecord(VoteRecord voteRecord);
    
    /**
     * 删除投票记录
     */
    void deleteVoteRecord(Long id);
    
    /**
     * 根据ID查找投票记录
     */
    VoteRecord findById(Long id);
    
    /**
     * 根据投票议题ID查找投票记录
     */
    List<VoteRecord> findByTopicId(Long topicId);
    
    /**
     * 根据投票人ID查找投票记录
     */
    List<VoteRecord> findByOwnerId(Long ownerId);
    
    /**
     * 根据投票议题ID、投票人ID和房产ID查找投票记录
     * 用于判断用户是否已经对某个议题进行过投票
     */
    List<VoteRecord> findByTopicIdAndVoterIdAndHouseId(Long topicId, Long voterId, Long houseId);
    
    Page<VoteRecord> findPage(Pageable pageable);
} 