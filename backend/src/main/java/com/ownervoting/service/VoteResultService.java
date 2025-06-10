package com.ownervoting.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 投票结果服务接口
 * 负责计算并缓存投票结果
 */
public interface VoteResultService {
    /**
     * 获取投票结果
     * @param topicId 投票主题ID
     * @return 投票结果统计
     */
    Map<String, Object> getVoteResult(Long topicId);
    
    /**
     * 获取投票选项结果详情
     * @param topicId 投票主题ID
     * @return 各选项投票结果列表
     */
    List<Map<String, Object>> getVoteOptionResults(Long topicId);
    
    /**
     * 获取某个投票选项的投票统计结果
     * @param optionId 选项ID
     * @return 统计结果，包含票数和权重
     */
    Map<String, Object> getOptionSummary(Long optionId);
    
    /**
     * 刷新投票结果缓存
     * @param topicId 投票主题ID
     */
    void refreshVoteResultCache(Long topicId);
    
    /**
     * 计算投票总票数
     * @param topicId 投票主题ID
     * @return 总票数
     */
    int countTotalVotes(Long topicId);
    
    /**
     * 计算投票总权重
     * @param topicId 投票主题ID
     * @return 总权重
     */
    BigDecimal calculateTotalWeight(Long topicId);
    
    /**
     * 获取投票参与率统计
     * @param topicId 投票主题ID
     * @return 包含户数参与率和面积参与率的统计信息
     */
    Map<String, Object> getParticipationStats(Long topicId);
    
    /**
     * 判断投票是否达到决议通过标准
     * @param topicId 投票主题ID
     * @param threshold 通过阈值（0.5表示过半数，0.67表示三分之二）
     * @return 是否达到决议通过标准
     */
    Map<String, Object> checkDecisionValidity(Long topicId, BigDecimal threshold);
    
    /**
     * 获取实时投票进度
     * @param topicId 投票主题ID
     * @return 实时进度信息，包括参与率、各选项得票率等
     */
    Map<String, Object> getRealTimeProgress(Long topicId);
    
    /**
     * 计算小区总户数
     * @param communityId 小区ID
     * @return 总户数
     */
    int getTotalHouseholds(Long communityId);
    
    /**
     * 计算小区总专有面积
     * @param communityId 小区ID
     * @return 总专有面积
     */
    BigDecimal getTotalArea(Long communityId);
} 