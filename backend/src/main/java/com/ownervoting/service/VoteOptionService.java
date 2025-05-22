package com.ownervoting.service;

import com.ownervoting.model.entity.VoteOption;
import java.util.List;

public interface VoteOptionService {
    VoteOption addVoteOption(VoteOption voteOption);
    void deleteVoteOption(Long id);
    VoteOption findById(Long id);
    List<VoteOption> findByTopicId(Long topicId);
} 