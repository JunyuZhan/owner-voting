package com.ownervoting.controller;

import com.ownervoting.model.entity.Suggestion;
import com.ownervoting.service.SuggestionService;
import com.ownervoting.model.vo.ApiResponse;
import com.ownervoting.model.dto.SuggestionAddDTO;
import com.ownervoting.model.vo.SuggestionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import java.util.Map;
import java.util.HashMap;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/suggestion")
@Validated
public class SuggestionController {

    @Autowired
    private SuggestionService suggestionService;

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/add")
    public ApiResponse<SuggestionVO> addSuggestion(@Valid @RequestBody SuggestionAddDTO dto) {
        Suggestion suggestion = new Suggestion();
        suggestion.setTitle(dto.getTitle());
        suggestion.setContent(dto.getContent());
        suggestion.setIsAnonymous(dto.getIsAnonymous());
        // community和owner应通过Service查找并set
        Suggestion saved = suggestionService.addSuggestion(suggestion);
        SuggestionVO vo = toVO(saved);
        return ApiResponse.success(vo);
    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteSuggestion(@PathVariable Long id) {
        suggestionService.deleteSuggestion(id);
        return ApiResponse.success(null);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{id}")
    public ApiResponse<SuggestionVO> getById(@PathVariable Long id) {
        Suggestion suggestion = suggestionService.findById(id);
        if (suggestion == null) return ApiResponse.success(null);
        return ApiResponse.success(toVO(suggestion));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/by-owner/{ownerId}")
    public ApiResponse<List<SuggestionVO>> getByOwnerId(@PathVariable Long ownerId) {
        List<Suggestion> list = suggestionService.findByOwnerId(ownerId);
        return ApiResponse.success(list.stream().map(this::toVO).toList());
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/suggestions/{id}/like")
    public ApiResponse<Void> likeSuggestion(@PathVariable Long id) {
        Suggestion suggestion = suggestionService.findById(id);
        if (suggestion == null) return ApiResponse.error(404, "建议不存在");
        suggestion.setLikeCount(suggestion.getLikeCount() == null ? 1 : suggestion.getLikeCount() + 1);
        suggestionService.addSuggestion(suggestion); // 假设 addSuggestion 可做保存
        return ApiResponse.success(null);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/page")
    public ApiResponse<Map<String, Object>> getPage(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        Page<Suggestion> paged = suggestionService.findPage(PageRequest.of(page, size));
        Map<String, Object> result = new HashMap<>();
        result.put("total", paged.getTotalElements());
        result.put("list", paged.getContent().stream().map(this::toVO).toList());
        return ApiResponse.success(result);
    }

    private SuggestionVO toVO(Suggestion s) {
        SuggestionVO vo = new SuggestionVO();
        vo.setId(s.getId());
        vo.setCommunityId(s.getCommunity() != null ? s.getCommunity().getId() : null);
        vo.setOwnerId(s.getOwner() != null ? s.getOwner().getId() : null);
        vo.setTitle(s.getTitle());
        vo.setContent(s.getContent());
        vo.setIsAnonymous(s.getIsAnonymous());
        vo.setStatus(s.getStatus());
        vo.setLikeCount(s.getLikeCount());
        vo.setDislikeCount(s.getDislikeCount());
        vo.setCreatedAt(s.getCreatedAt());
        vo.setUpdatedAt(s.getUpdatedAt());
        return vo;
    }
} 