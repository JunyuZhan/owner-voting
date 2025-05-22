package com.ownervoting.controller;

import com.ownervoting.model.entity.VoteTopic;
import com.ownervoting.service.VoteTopicService;
import com.ownervoting.model.vo.ApiResponse;
import com.ownervoting.model.dto.VoteTopicAddDTO;
import com.ownervoting.model.vo.VoteTopicVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import com.ownervoting.service.VoteRecordService;
import com.ownervoting.service.VoteOptionService;
import com.ownervoting.service.VoteResultService;
import com.ownervoting.model.entity.VoteRecord;
import com.ownervoting.model.entity.VoteOption;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/vote-topic")
@Validated
public class VoteTopicController {

    @Autowired
    private VoteTopicService voteTopicService;
    @Autowired
    private VoteRecordService voteRecordService;
    @Autowired
    private VoteOptionService voteOptionService;
    @Autowired
    private VoteResultService voteResultService;

    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','COMMUNITY_ADMIN','OPERATOR')")
    @PostMapping("/add")
    public ApiResponse<VoteTopicVO> addVoteTopic(@Valid @RequestBody VoteTopicAddDTO dto) {
        VoteTopic topic = new VoteTopic();
        topic.setTitle(dto.getTitle());
        topic.setDescription(dto.getDescription());
        topic.setStartTime(dto.getStartTime());
        topic.setEndTime(dto.getEndTime());
        topic.setIsAreaWeighted(dto.getIsAreaWeighted());
        topic.setIsRealName(dto.getIsRealName());
        topic.setIsResultPublic(dto.getIsResultPublic());
        topic.setStatus(dto.getStatus());
        // community和createdBy应通过Service查找并set
        VoteTopic saved = voteTopicService.addTopic(topic);
        VoteTopicVO vo = toVO(saved);
        return ApiResponse.success(vo);
    }

    @PreAuthorize("hasAnyRole('SYSTEM_ADMIN','COMMUNITY_ADMIN','OPERATOR')")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteVoteTopic(@PathVariable Long id) {
        voteTopicService.deleteTopic(id);
        return ApiResponse.success(null);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ApiResponse<VoteTopicVO> getById(@PathVariable Long id) {
        VoteTopic topic = voteTopicService.findById(id);
        if (topic == null) return ApiResponse.success(null);
        return ApiResponse.success(toVO(topic));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/all")
    public ApiResponse<List<VoteTopicVO>> getAll() {
        List<VoteTopic> list = voteTopicService.findAll();
        return ApiResponse.success(list.stream().map(this::toVO).toList());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/votes")
    public ApiResponse<List<VoteTopicVO>> getVoteList() {
        List<VoteTopic> list = voteTopicService.findAll();
        return ApiResponse.success(list.stream().map(this::toVO).toList());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/votes/{id}")
    public ApiResponse<VoteTopicVO> getVoteDetail(@PathVariable Long id) {
        VoteTopic topic = voteTopicService.findById(id);
        if (topic == null) return ApiResponse.success(null);
        return ApiResponse.success(toVO(topic));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/votes/{id}/result")
    public ApiResponse<?> getVoteResult(@PathVariable Long id) {
        // 使用投票结果服务获取结果，利用缓存提高性能
        return ApiResponse.success(voteResultService.getVoteResult(id));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/page")
    public ApiResponse<Map<String, Object>> getPage(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<VoteTopic> paged = voteTopicService.findPage(PageRequest.of(page, size));
        Map<String, Object> result = new HashMap<>();
        result.put("total", paged.getTotalElements());
        result.put("list", paged.getContent().stream().map(this::toVO).toList());
        return ApiResponse.success(result);
    }

    private VoteTopicVO toVO(VoteTopic topic) {
        VoteTopicVO vo = new VoteTopicVO();
        vo.setId(topic.getId());
        vo.setCommunityId(topic.getCommunity() != null ? topic.getCommunity().getId() : null);
        vo.setTitle(topic.getTitle());
        vo.setDescription(topic.getDescription());
        vo.setStartTime(topic.getStartTime());
        vo.setEndTime(topic.getEndTime());
        vo.setIsAreaWeighted(topic.getIsAreaWeighted());
        vo.setIsRealName(topic.getIsRealName());
        vo.setIsResultPublic(topic.getIsResultPublic());
        vo.setStatus(topic.getStatus());
        // createdBy类型为String，VO为Long，实际应做转换
        vo.setCreatedBy(null);
        return vo;
    }
} 