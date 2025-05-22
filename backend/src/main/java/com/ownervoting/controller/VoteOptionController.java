package com.ownervoting.controller;

import com.ownervoting.model.entity.VoteOption;
import com.ownervoting.service.VoteOptionService;
import com.ownervoting.model.vo.ApiResponse;
import com.ownervoting.model.dto.VoteOptionAddDTO;
import com.ownervoting.model.vo.VoteOptionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/vote-option")
@Validated
public class VoteOptionController {

    @Autowired
    private VoteOptionService voteOptionService;

    @PostMapping("/add")
    public ApiResponse<VoteOptionVO> addVoteOption(@Valid @RequestBody VoteOptionAddDTO dto) {
        VoteOption option = new VoteOption();
        option.setOptionText(dto.getOptionText());
        option.setSortOrder(dto.getSortOrder());
        // topic应通过Service查找并set
        VoteOption saved = voteOptionService.addVoteOption(option);
        VoteOptionVO vo = toVO(saved);
        return ApiResponse.success(vo);
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteVoteOption(@PathVariable Long id) {
        voteOptionService.deleteVoteOption(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}")
    public ApiResponse<VoteOptionVO> getById(@PathVariable Long id) {
        VoteOption option = voteOptionService.findById(id);
        if (option == null) return ApiResponse.success(null);
        return ApiResponse.success(toVO(option));
    }

    @GetMapping("/by-topic/{topicId}")
    public ApiResponse<List<VoteOptionVO>> getByTopicId(@PathVariable Long topicId) {
        List<VoteOption> list = voteOptionService.findByTopicId(topicId);
        return ApiResponse.success(list.stream().map(this::toVO).toList());
    }

    private VoteOptionVO toVO(VoteOption o) {
        VoteOptionVO vo = new VoteOptionVO();
        vo.setId(o.getId());
        vo.setTopicId(o.getTopic() != null ? o.getTopic().getId() : null);
        vo.setOptionText(o.getOptionText());
        vo.setSortOrder(o.getSortOrder());
        return vo;
    }
} 