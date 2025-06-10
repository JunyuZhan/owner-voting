package com.ownervoting.controller;

import com.ownervoting.model.entity.Advertisement;
import com.ownervoting.service.AdvertisementService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/ad")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdController {

    private final AdvertisementService advertisementService;

    /**
     * 测试数据库连接和广告数据（临时调试接口）
     */
    @GetMapping("/test")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<Map<String, Object>> testAd() {
        try {
            List<Advertisement> allAds = advertisementService.findAll();
            log.info("数据库中总共有 {} 条广告记录", allAds.size());
            
            List<Advertisement> activeAds = advertisementService.findActiveAds();
            log.info("数据库中有 {} 条激活的广告记录", activeAds.size());
            
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "测试成功",
                "data", Map.of(
                    "totalAds", allAds.size(),
                    "activeAds", activeAds.size(),
                    "allAds", allAds,
                    "activeAds", activeAds
                )
            ));
        } catch (Exception e) {
            log.error("测试失败", e);
            return ResponseEntity.ok(Map.of(
                "code", 500,
                "message", "测试失败: " + e.getMessage(),
                "data", null
            ));
        }
    }

    /**
     * 获取当前展示的广告（公开接口）
     */
    @GetMapping("/current")
    public ResponseEntity<Map<String, Object>> getCurrentAd() {
        log.info("获取当前展示的广告");
        
        // 暂时返回空广告，避免阻塞前端
        return ResponseEntity.ok(Map.of(
            "code", 200,
            "message", "暂无广告",
            "data", null
        ));
    }

    /**
     * 获取所有广告列表（仅系统管理员）
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<Map<String, Object>> getAdList() {
        try {
            List<Advertisement> ads = advertisementService.findAll();
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", ads
            ));
        } catch (Exception e) {
            log.error("获取广告列表失败", e);
            return ResponseEntity.ok(Map.of(
                "code", 500,
                "message", "获取失败",
                "data", Collections.emptyList()
            ));
        }
    }

    /**
     * 记录广告点击（用于统计）
     */
    @PostMapping("/click/{adId}")
    public ResponseEntity<Map<String, Object>> recordClick(@PathVariable Long adId) {
        try {
            advertisementService.recordClick(adId);
            
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "记录成功"
            ));
        } catch (Exception e) {
            log.error("记录广告点击失败", e);
            return ResponseEntity.ok(Map.of(
                "code", 500,
                "message", "记录失败"
            ));
        }
    }

    /**
     * 创建广告（仅系统管理员）
     */
    @PostMapping("/create")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<Map<String, Object>> createAd(@RequestBody Advertisement advertisement) {
        try {
            Advertisement savedAd = advertisementService.save(advertisement);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "创建成功",
                "data", savedAd
            ));
        } catch (Exception e) {
            log.error("创建广告失败", e);
            return ResponseEntity.ok(Map.of(
                "code", 500,
                "message", "创建失败",
                "data", null
            ));
        }
    }

    /**
     * 更新广告（仅系统管理员）
     */
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<Map<String, Object>> updateAd(@PathVariable Long id, @RequestBody Advertisement advertisement) {
        try {
            if (!advertisementService.findById(id).isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "code", 404,
                    "message", "广告不存在",
                    "data", null
                ));
            }
            
            advertisement.setId(id);
            Advertisement savedAd = advertisementService.save(advertisement);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "更新成功",
                "data", savedAd
            ));
        } catch (Exception e) {
            log.error("更新广告失败", e);
            return ResponseEntity.ok(Map.of(
                "code", 500,
                "message", "更新失败",
                "data", null
            ));
        }
    }

    /**
     * 删除广告（仅系统管理员）
     */
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteAd(@PathVariable Long id) {
        try {
            if (!advertisementService.findById(id).isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "code", 404,
                    "message", "广告不存在"
                ));
            }
            
            advertisementService.deleteById(id);
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "删除成功"
            ));
        } catch (Exception e) {
            log.error("删除广告失败", e);
            return ResponseEntity.ok(Map.of(
                "code", 500,
                "message", "删除失败"
            ));
        }
    }

    /**
     * 切换广告状态（仅系统管理员）
     */
    @PostMapping("/toggle/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<Map<String, Object>> toggleAdStatus(@PathVariable Long id) {
        try {
            if (!advertisementService.findById(id).isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "code", 404,
                    "message", "广告不存在"
                ));
            }
            
            Advertisement ad = advertisementService.findById(id).get();
            ad.setIsActive(!ad.getIsActive());
            Advertisement savedAd = advertisementService.save(ad);
            
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "状态切换成功",
                "data", savedAd
            ));
        } catch (Exception e) {
            log.error("切换广告状态失败", e);
            return ResponseEntity.ok(Map.of(
                "code", 500,
                "message", "切换失败"
            ));
        }
    }

    /**
     * 获取广告详情（仅系统管理员）
     */
    @GetMapping("/detail/{id}")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<Map<String, Object>> getAdDetail(@PathVariable Long id) {
        try {
            if (!advertisementService.findById(id).isPresent()) {
                return ResponseEntity.ok(Map.of(
                    "code", 404,
                    "message", "广告不存在",
                    "data", null
                ));
            }
            
            Advertisement ad = advertisementService.findById(id).get();
            return ResponseEntity.ok(Map.of(
                "code", 200,
                "message", "获取成功",
                "data", ad
            ));
        } catch (Exception e) {
            log.error("获取广告详情失败", e);
            return ResponseEntity.ok(Map.of(
                "code", 500,
                "message", "获取失败",
                "data", null
            ));
        }
    }
}