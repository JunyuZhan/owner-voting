package com.ownervoting.controller;

import com.ownervoting.exception.BusinessException;
import com.ownervoting.exception.NotFoundException;
import com.ownervoting.model.dto.VoteRecordAddDTO;
import com.ownervoting.model.entity.House;
import com.ownervoting.model.entity.Owner;
import com.ownervoting.model.entity.VoteOption;
import com.ownervoting.model.entity.VoteRecord;
import com.ownervoting.model.entity.VoteTopic;
import com.ownervoting.model.vo.ApiResponse;
import com.ownervoting.model.vo.VoteRecordVO;
import com.ownervoting.service.HouseService;
import com.ownervoting.service.OwnerService;
import com.ownervoting.service.VoteOptionService;
import com.ownervoting.service.VoteRecordService;
import com.ownervoting.service.VoteTopicService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class VoteRecordController {

    @Autowired
    private VoteRecordService voteRecordService;

    @Autowired
    private VoteTopicService voteTopicService;

    @Autowired
    private VoteOptionService voteOptionService;

    @Autowired
    private OwnerService ownerService;

    @Autowired
    private HouseService houseService;

    @GetMapping("/votes/{topicId}/records")
    public ApiResponse<List<VoteRecordVO>> getVoteRecords(@PathVariable Long topicId, @RequestParam(required = false) Long voterId) {
        // 查询投票议题
        VoteTopic topic = voteTopicService.findById(topicId);
        if (topic == null) {
            throw NotFoundException.of("投票议题", topicId);
        }

        // 查询投票记录
        List<VoteRecord> records = voteRecordService.findByTopicId(topicId);
        if (voterId != null) {
            records = records.stream().filter(r -> r.getVoter() != null && r.getVoter().getId().equals(voterId)).collect(Collectors.toList());
        }

        // 转换为VO
        List<VoteRecordVO> vos = records.stream().map(r -> {
            VoteRecordVO vo = new VoteRecordVO();
            vo.setId(r.getId());
            if (r.getVoter() != null) {
                vo.setVoterId(r.getVoter().getId());
                vo.setVoterName(r.getVoter().getName());
            }
            if (r.getHouse() != null) {
                vo.setHouseId(r.getHouse().getId());
                vo.setHouseAddress(r.getHouse().getAddress());
            }
            vo.setTopicId(r.getTopic().getId());
            vo.setOptionId(r.getOption().getId());
            vo.setOptionText(r.getOption().getOptionText());
            vo.setVoteWeight(r.getVoteWeight());
            vo.setVoteTime(r.getVoteTime());
            return vo;
        }).collect(Collectors.toList());

        return ApiResponse.success(vos);
    }

    @PostMapping("/votes/{id}/cast")
    @Transactional
    public ApiResponse<VoteRecordVO> castVote(@PathVariable Long id, @Valid @RequestBody VoteRecordAddDTO dto) {
        // 校验投票议题
        VoteTopic topic = voteTopicService.findById(id);
        if (topic == null) {
            throw NotFoundException.of("投票议题", id);
        }
        
        // 校验投票时间
        LocalDateTime now = LocalDateTime.now();
        if (topic.getStartTime() != null && now.isBefore(topic.getStartTime())) {
            throw new BusinessException(400, "投票尚未开始");
        }
        if (topic.getEndTime() != null && now.isAfter(topic.getEndTime())) {
            throw new BusinessException(400, "投票已结束");
        }
        
        // 校验用户
        Owner owner = ownerService.findById(dto.getVoterId());
        if (owner == null) {
            throw NotFoundException.of("业主", dto.getVoterId());
        }
        if (!Boolean.TRUE.equals(owner.getIsVerified())) {
            throw new BusinessException(403, "用户未认证，无法投票");
        }
        
        // 校验房产
        House house = houseService.findById(dto.getHouseId());
        if (house == null) {
            throw NotFoundException.of("房产", dto.getHouseId());
        }
        
        // 校验是否已投票（同一议题、同一房产、同一用户只能投一次）
        List<VoteRecord> records = voteRecordService.findByTopicId(id);
        boolean hasVoted = records.stream().anyMatch(r -> 
            r.getVoter() != null && r.getVoter().getId().equals(dto.getVoterId()) && 
            r.getHouse() != null && r.getHouse().getId().equals(dto.getHouseId()));
            
        if (hasVoted) {
            throw new BusinessException(409, "您已投过票");
        }
        
        // 校验选项
        VoteOption option = voteOptionService.findById(dto.getOptionId());
        if (option == null) {
            throw NotFoundException.of("投票选项", dto.getOptionId());
        }
        
        // 计算投票权重（如果是按面积计票）
        BigDecimal voteWeight = BigDecimal.ONE;
        if (Boolean.TRUE.equals(topic.getIsAreaWeighted()) && house.getArea() != null) {
            voteWeight = house.getArea();
        }
        
        // 构建投票记录
        VoteRecord record = new VoteRecord();
        record.setTopic(topic);
        record.setOption(option);
        record.setHouse(house);
        record.setVoter(owner);
        record.setVoteWeight(voteWeight);
        record.setVoteTime(LocalDateTime.now());
        record.setIpAddress(dto.getIpAddress());
        record.setDeviceInfo(dto.getDeviceInfo());
        
        // 保存投票记录
        record = voteRecordService.addVoteRecord(record);
        
        // 转换为VO返回
        VoteRecordVO vo = new VoteRecordVO();
        vo.setId(record.getId());
        vo.setVoterId(record.getVoter().getId());
        vo.setVoterName(record.getVoter().getName());
        vo.setHouseId(record.getHouse().getId());
        vo.setHouseAddress(record.getHouse().getAddress());
        vo.setTopicId(record.getTopic().getId());
        vo.setOptionId(record.getOption().getId());
        vo.setOptionText(record.getOption().getOptionText());
        vo.setVoteWeight(record.getVoteWeight());
        vo.setVoteTime(record.getVoteTime());
        
        return ApiResponse.success(vo);
    }
} 