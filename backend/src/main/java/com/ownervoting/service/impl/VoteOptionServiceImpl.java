package com.ownervoting.service.impl;

import com.ownervoting.model.entity.VoteOption;
import com.ownervoting.repository.VoteOptionRepository;
import com.ownervoting.service.VoteOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VoteOptionServiceImpl implements VoteOptionService {

    @Autowired
    private VoteOptionRepository voteOptionRepository;

    @Override
    @Transactional
    public VoteOption addVoteOption(VoteOption voteOption) {
        return voteOptionRepository.save(voteOption);
    }

    @Override
    @Transactional
    public void deleteVoteOption(Long id) {
        voteOptionRepository.deleteById(id);
    }

    @Override
    public VoteOption findById(Long id) {
        return voteOptionRepository.findById(id).orElse(null);
    }

    @Override
    public List<VoteOption> findByTopicId(Long topicId) {
        return voteOptionRepository.findAll().stream()
                .filter(o -> (o.getVoteTopic() != null && o.getVoteTopic().getId().equals(topicId)) || (o.getTopic() != null && o.getTopic().getId().equals(topicId)))
                .toList();
    }
} 