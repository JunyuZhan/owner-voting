package com.ownervoting.repository;

import com.ownervoting.model.entity.Advertisement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long> {
    
    /**
     * 查找所有激活的广告
     */
    List<Advertisement> findByIsActiveTrue();
    
    /**
     * 查找在有效期内的激活广告
     */
    @Query("SELECT a FROM Advertisement a WHERE a.isActive = true " +
           "AND (a.startTime IS NULL OR a.startTime <= :now) " +
           "AND (a.endTime IS NULL OR a.endTime >= :now)")
    List<Advertisement> findActiveAdsInValidPeriod(LocalDateTime now);
    
    /**
     * 按类型查找广告
     */
    List<Advertisement> findByTypeAndIsActiveTrue(Advertisement.AdType type);
    
    /**
     * 按权重排序查找激活广告
     */
    List<Advertisement> findByIsActiveTrueOrderByWeightDesc();
} 