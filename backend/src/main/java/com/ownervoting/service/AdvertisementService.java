package com.ownervoting.service;

import com.ownervoting.model.entity.Advertisement;
import com.ownervoting.repository.AdvertisementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final Random random = new Random();

    /**
     * 获取所有广告
     */
    public List<Advertisement> findAll() {
        return advertisementRepository.findAll();
    }

    /**
     * 根据权重随机选择一个有效的广告
     */
    public Optional<Advertisement> selectRandomAd() {
        try {
            List<Advertisement> activeAds = advertisementRepository.findActiveAdsInValidPeriod(LocalDateTime.now());
            
            if (activeAds == null || activeAds.isEmpty()) {
                log.debug("没有找到有效的广告");
                return Optional.empty();
            }
            
            // 计算总权重，确保不会有空指针
            int totalWeight = activeAds.stream()
                    .mapToInt(ad -> ad.getWeight() != null ? ad.getWeight() : 1)
                    .sum();
            
            if (totalWeight <= 0) {
                // 如果没有权重，随机选择
                log.debug("总权重为0，随机选择广告");
                return Optional.of(activeAds.get(random.nextInt(activeAds.size())));
            }
            
            // 基于权重随机选择
            int randomWeight = random.nextInt(totalWeight);
            int currentWeight = 0;
            
            for (Advertisement ad : activeAds) {
                int adWeight = ad.getWeight() != null ? ad.getWeight() : 1;
                currentWeight += adWeight;
                if (randomWeight < currentWeight) {
                    log.debug("选择了广告: {}, 权重: {}", ad.getTitle(), adWeight);
                    return Optional.of(ad);
                }
            }
            
            // 备选方案：返回第一个
            log.debug("使用备选方案，返回第一个广告");
            return Optional.of(activeAds.get(0));
            
        } catch (Exception e) {
            log.error("选择广告时发生错误", e);
            return Optional.empty();
        }
    }

    /**
     * 记录广告点击
     */
    @Transactional
    public void recordClick(Long adId) {
        try {
            advertisementRepository.findById(adId).ifPresent(ad -> {
                Long currentClickCount = ad.getClickCount() != null ? ad.getClickCount() : 0L;
                ad.setClickCount(currentClickCount + 1);
                advertisementRepository.save(ad);
                log.info("广告点击记录: ID={}, 标题={}, 总点击数={}", adId, ad.getTitle(), ad.getClickCount());
            });
        } catch (Exception e) {
            log.error("记录广告点击失败: adId={}", adId, e);
        }
    }

    /**
     * 记录广告展示
     */
    @Transactional
    public void recordView(Long adId) {
        try {
            advertisementRepository.findById(adId).ifPresent(ad -> {
                Long currentViewCount = ad.getViewCount() != null ? ad.getViewCount() : 0L;
                ad.setViewCount(currentViewCount + 1);
                advertisementRepository.save(ad);
                log.debug("广告展示记录: ID={}, 标题={}, 总展示数={}", adId, ad.getTitle(), ad.getViewCount());
            });
        } catch (Exception e) {
            log.error("记录广告展示失败: adId={}", adId, e);
        }
    }

    /**
     * 保存广告
     */
    @Transactional
    public Advertisement save(Advertisement advertisement) {
        return advertisementRepository.save(advertisement);
    }

    /**
     * 根据ID查找广告
     */
    public Optional<Advertisement> findById(Long id) {
        return advertisementRepository.findById(id);
    }

    /**
     * 删除广告
     */
    @Transactional
    public void deleteById(Long id) {
        advertisementRepository.deleteById(id);
    }

    /**
     * 切换广告状态
     */
    @Transactional
    public void toggleStatus(Long id) {
        advertisementRepository.findById(id).ifPresent(ad -> {
            ad.setIsActive(!ad.getIsActive());
            advertisementRepository.save(ad);
            log.info("广告状态切换: ID={}, 新状态={}", id, ad.getIsActive());
        });
    }

    /**
     * 获取激活的广告列表
     */
    public List<Advertisement> findActiveAds() {
        return advertisementRepository.findByIsActiveTrue();
    }
} 