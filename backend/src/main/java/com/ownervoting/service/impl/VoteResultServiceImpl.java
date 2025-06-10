package com.ownervoting.service.impl;

import com.ownervoting.config.CacheConfig;
import com.ownervoting.model.entity.VoteOption;
import com.ownervoting.model.entity.VoteRecord;
import com.ownervoting.model.entity.VoteTopic;
import com.ownervoting.model.entity.House;
import com.ownervoting.service.VoteOptionService;
import com.ownervoting.service.VoteRecordService;
import com.ownervoting.service.VoteResultService;
import com.ownervoting.service.VoteTopicService;
import com.ownervoting.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class VoteResultServiceImpl implements VoteResultService {

    @Autowired
    private VoteTopicService voteTopicService;

    @Autowired
    private VoteOptionService voteOptionService;

    @Autowired
    private VoteRecordService voteRecordService;
    
    @Autowired
    private HouseService houseService;

    @Override
    @Cacheable(value = CacheConfig.VOTE_CACHE, key = "'vote_result:' + #topicId")
    public Map<String, Object> getVoteResult(Long topicId) {
        VoteTopic topic = voteTopicService.findById(topicId);
        if (topic == null) {
            return new HashMap<>();
        }

        List<Map<String, Object>> optionResults = getVoteOptionResults(topicId);
        int totalVotes = countTotalVotes(topicId);
        BigDecimal totalWeight = calculateTotalWeight(topicId);

        Map<String, Object> result = new HashMap<>();
        result.put("topicId", topicId);
        result.put("topicTitle", topic.getTitle());
        result.put("totalVotes", totalVotes);
        result.put("totalWeight", totalWeight);
        result.put("options", optionResults);
        result.put("isAreaWeighted", topic.getIsAreaWeighted());
        result.put("isRealName", topic.getIsRealName());
        result.put("isResultPublic", topic.getIsResultPublic());
        result.put("status", topic.getStatus());
        
        // 添加参与率统计
        Map<String, Object> participationStats = getParticipationStats(topicId);
        result.put("participationStats", participationStats);
        
        // 添加决议有效性判断
        Map<String, Object> majorityDecision = checkDecisionValidity(topicId, new BigDecimal("0.5"));
        Map<String, Object> twoThirdsDecision = checkDecisionValidity(topicId, new BigDecimal("0.67"));
        result.put("majorityDecision", majorityDecision);
        result.put("twoThirdsDecision", twoThirdsDecision);
        
        return result;
    }

    @Override
    @Cacheable(value = CacheConfig.VOTE_CACHE, key = "'vote_options_result:' + #topicId")
    public List<Map<String, Object>> getVoteOptionResults(Long topicId) {
        List<VoteOption> options = voteOptionService.findByTopicId(topicId);
        List<VoteRecord> records = voteRecordService.findByTopicId(topicId);
        
        Map<Long, Integer> countMap = new HashMap<>();
        Map<Long, BigDecimal> weightMap = new HashMap<>();
        
        // 初始化计数器
        for (VoteOption opt : options) {
            countMap.put(opt.getId(), 0);
            weightMap.put(opt.getId(), BigDecimal.ZERO);
        }
        
        // 统计投票数据
        for (VoteRecord rec : records) {
            if (rec.getOption() != null) {
                Long optId = rec.getOption().getId();
                if (countMap.containsKey(optId)) {
                    countMap.put(optId, countMap.get(optId) + 1);
                    weightMap.put(optId, weightMap.get(optId).add(
                            rec.getVoteWeight() != null ? rec.getVoteWeight() : BigDecimal.ONE));
                }
            }
        }
        
        // 计算投票百分比
        int totalCount = countMap.values().stream().mapToInt(Integer::intValue).sum();
        BigDecimal totalWeight = weightMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return options.stream().map(opt -> {
            Map<String, Object> m = new HashMap<>();
            m.put("optionId", opt.getId());
            m.put("optionText", opt.getOptionText());
            m.put("voteCount", countMap.get(opt.getId()));
            m.put("voteWeight", weightMap.get(opt.getId()));
            
            // 计算百分比
            int count = countMap.get(opt.getId());
            BigDecimal weight = weightMap.get(opt.getId());
            
            if (totalCount > 0) {
                double countPercent = (double) count / totalCount * 100;
                m.put("countPercent", Math.round(countPercent * 100) / 100.0); // 保留两位小数
            } else {
                m.put("countPercent", 0.0);
            }
            
            if (totalWeight.compareTo(BigDecimal.ZERO) > 0) {
                double weightPercent = weight.divide(totalWeight, 4, BigDecimal.ROUND_HALF_UP)
                        .multiply(new BigDecimal("100"))
                        .doubleValue();
                m.put("weightPercent", Math.round(weightPercent * 100) / 100.0); // 保留两位小数
            } else {
                m.put("weightPercent", 0.0);
            }
            
            return m;
        }).collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = CacheConfig.VOTE_CACHE, key = "'option_summary:' + #optionId")
    public Map<String, Object> getOptionSummary(Long optionId) {
        List<VoteRecord> records = voteRecordService.findByTopicId(
                voteOptionService.findById(optionId).getTopic().getId());
        
        int count = 0;
        BigDecimal weight = BigDecimal.ZERO;
        
        for (VoteRecord rec : records) {
            if (rec.getOption() != null && rec.getOption().getId().equals(optionId)) {
                count++;
                weight = weight.add(rec.getVoteWeight() != null ? rec.getVoteWeight() : BigDecimal.ONE);
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("optionId", optionId);
        result.put("voteCount", count);
        result.put("voteWeight", weight);
        
        return result;
    }

    @Override
    @CacheEvict(value = CacheConfig.VOTE_CACHE, allEntries = true)
    public void refreshVoteResultCache(Long topicId) {
        // 清空缓存，下次访问时会重新计算
    }

    @Override
    public int countTotalVotes(Long topicId) {
        return voteRecordService.findByTopicId(topicId).size();
    }

    @Override
    public BigDecimal calculateTotalWeight(Long topicId) {
        return voteRecordService.findByTopicId(topicId).stream()
                .map(record -> record.getVoteWeight() != null ? record.getVoteWeight() : BigDecimal.ONE)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    @Override
    public Map<String, Object> getParticipationStats(Long topicId) {
        VoteTopic topic = voteTopicService.findById(topicId);
        if (topic == null || topic.getCommunity() == null) {
            return new HashMap<>();
        }
        
        Long communityId = topic.getCommunity().getId();
        
        // 获取小区总户数和总面积
        int totalHouseholds = getTotalHouseholds(communityId);
        BigDecimal totalArea = getTotalArea(communityId);
        
        // 获取已投票的户数和面积
        List<VoteRecord> records = voteRecordService.findByTopicId(topicId);
        int votedHouseholds = records.size();
        BigDecimal votedWeight = calculateTotalWeight(topicId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalHouseholds", totalHouseholds);
        stats.put("votedHouseholds", votedHouseholds);
        stats.put("totalArea", totalArea);
        stats.put("votedWeight", votedWeight);
        
        // 计算参与率
        if (totalHouseholds > 0) {
            double householdParticipationRate = (double) votedHouseholds / totalHouseholds * 100;
            stats.put("householdParticipationRate", Math.round(householdParticipationRate * 100) / 100.0);
        } else {
            stats.put("householdParticipationRate", 0.0);
        }
        
        if (totalArea.compareTo(BigDecimal.ZERO) > 0) {
            double areaParticipationRate = votedWeight.divide(totalArea, 4, RoundingMode.HALF_UP)
                    .multiply(new BigDecimal("100")).doubleValue();
            stats.put("areaParticipationRate", Math.round(areaParticipationRate * 100) / 100.0);
        } else {
            stats.put("areaParticipationRate", 0.0);
        }
        
        return stats;
    }
    
    @Override
    public Map<String, Object> checkDecisionValidity(Long topicId, BigDecimal threshold) {
        Map<String, Object> participationStats = getParticipationStats(topicId);
        List<Map<String, Object>> optionResults = getVoteOptionResults(topicId);
        
        double householdParticipationRate = (Double) participationStats.get("householdParticipationRate");
        double areaParticipationRate = (Double) participationStats.get("areaParticipationRate");
        
        double requiredThreshold = threshold.multiply(new BigDecimal("100")).doubleValue();
        
        // 检查参与率是否达标
        boolean householdThresholdMet = householdParticipationRate >= requiredThreshold;
        boolean areaThresholdMet = areaParticipationRate >= requiredThreshold;
        boolean participationValid = householdThresholdMet && areaThresholdMet;
        
        Map<String, Object> validity = new HashMap<>();
        validity.put("threshold", requiredThreshold);
        validity.put("householdThresholdMet", householdThresholdMet);
        validity.put("areaThresholdMet", areaThresholdMet);
        validity.put("participationValid", participationValid);
        validity.put("householdParticipationRate", householdParticipationRate);
        validity.put("areaParticipationRate", areaParticipationRate);
        
        // 检查每个选项是否达到通过标准
        if (participationValid && !optionResults.isEmpty()) {
            Map<String, Object> topOption = optionResults.stream()
                    .max((o1, o2) -> {
                        Double count1 = (Double) o1.get("countPercent");
                        Double count2 = (Double) o2.get("countPercent");
                        return count1.compareTo(count2);
                    }).orElse(new HashMap<>());
                    
            validity.put("topOption", topOption);
            
            Double topOptionCountPercent = (Double) topOption.get("countPercent");
            Double topOptionWeightPercent = (Double) topOption.get("weightPercent");
            
            boolean optionHouseholdThresholdMet = topOptionCountPercent != null && topOptionCountPercent >= requiredThreshold;
            boolean optionAreaThresholdMet = topOptionWeightPercent != null && topOptionWeightPercent >= requiredThreshold;
            boolean decisionValid = optionHouseholdThresholdMet && optionAreaThresholdMet;
            
            validity.put("optionHouseholdThresholdMet", optionHouseholdThresholdMet);
            validity.put("optionAreaThresholdMet", optionAreaThresholdMet);
            validity.put("decisionValid", decisionValid);
        } else {
            validity.put("decisionValid", false);
        }
        
        return validity;
    }
    
    @Override
    public Map<String, Object> getRealTimeProgress(Long topicId) {
        Map<String, Object> result = getVoteResult(topicId);
        Map<String, Object> progress = new HashMap<>();
        
        progress.put("topicId", topicId);
        progress.put("totalVotes", result.get("totalVotes"));
        progress.put("totalWeight", result.get("totalWeight"));
        progress.put("participationStats", result.get("participationStats"));
        progress.put("options", result.get("options"));
        progress.put("majorityDecision", result.get("majorityDecision"));
        progress.put("twoThirdsDecision", result.get("twoThirdsDecision"));
        progress.put("lastUpdated", System.currentTimeMillis());
        
        return progress;
    }
    
    @Override
    public int getTotalHouseholds(Long communityId) {
        List<House> houses = houseService.findByCommunityIdAndVerificationStatus(
                communityId, House.VerificationStatus.APPROVED);
        return houses.size();
    }
    
    @Override
    public BigDecimal getTotalArea(Long communityId) {
        List<House> houses = houseService.findByCommunityIdAndVerificationStatus(
                communityId, House.VerificationStatus.APPROVED);
        return houses.stream()
                .map(house -> house.getArea() != null ? house.getArea() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}