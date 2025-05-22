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
} 