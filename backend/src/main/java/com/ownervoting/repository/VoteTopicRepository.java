package com.ownervoting.repository;

import com.ownervoting.model.entity.VoteTopic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VoteTopicRepository extends JpaRepository<VoteTopic, Long> {
    /**
     * 根据小区ID查询投票主题
     */
    List<VoteTopic> findByCommunityId(Long communityId);
    
    /**
     * 查询指定状态且在时间范围内的投票主题
     */
    List<VoteTopic> findByStatusAndStartTimeBeforeAndEndTimeAfter(String status, LocalDateTime now, LocalDateTime now2);
} 