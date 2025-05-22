package com.ownervoting.repository;

import com.ownervoting.model.entity.VoteRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRecordRepository extends JpaRepository<VoteRecord, Long> {
    /**
     * 根据议题ID查询投票记录
     */
    @Query("SELECT v FROM VoteRecord v WHERE v.topic.id = :topicId")
    List<VoteRecord> findByTopicId(@Param("topicId") Long topicId);
    
    /**
     * 根据投票人ID查询投票记录
     */
    @Query("SELECT v FROM VoteRecord v WHERE v.voter.id = :voterId")
    List<VoteRecord> findByVoterId(@Param("voterId") Long voterId);
    
    /**
     * 根据议题ID、投票人ID和房产ID查询投票记录
     * 用于防止重复投票
     */
    @Query("SELECT v FROM VoteRecord v WHERE v.topic.id = :topicId AND v.voter.id = :voterId AND v.house.id = :houseId")
    List<VoteRecord> findByTopicIdAndVoterIdAndHouseId(
            @Param("topicId") Long topicId, 
            @Param("voterId") Long voterId, 
            @Param("houseId") Long houseId);
} 