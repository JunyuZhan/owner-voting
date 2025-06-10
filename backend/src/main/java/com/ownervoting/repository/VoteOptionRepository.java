package com.ownervoting.repository;

import com.ownervoting.model.entity.VoteOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VoteOptionRepository extends JpaRepository<VoteOption, Long> {
    
    @Query("SELECT vo FROM VoteOption vo WHERE vo.topic.id = :topicId")
    List<VoteOption> findByTopicId(@Param("topicId") Long topicId);
}