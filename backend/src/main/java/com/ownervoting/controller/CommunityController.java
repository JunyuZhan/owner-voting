package com.ownervoting.controller;

import com.ownervoting.model.entity.Community;
import com.ownervoting.service.CommunityService;
import com.ownervoting.model.vo.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/community")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @PostMapping("/add")
    public ApiResponse<Community> addCommunity(@RequestBody Community community) {
        return ApiResponse.success(communityService.addCommunity(community));
    }

    @DeleteMapping("/delete/{id}")
    public ApiResponse<Void> deleteCommunity(@PathVariable Long id) {
        communityService.deleteCommunity(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/{id}")
    public ApiResponse<Community> getById(@PathVariable Long id) {
        return ApiResponse.success(communityService.findById(id));
    }

    @GetMapping("/all")
    public ApiResponse<List<Community>> getAll() {
        return ApiResponse.success(communityService.findAll());
    }
} 