package com.ownervoting.service;

import com.ownervoting.model.entity.VoteOption;
import com.ownervoting.model.entity.VoteRecord;
import com.ownervoting.model.entity.VoteTopic;
import com.ownervoting.service.impl.VoteResultServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class VoteResultServiceTest {

    @Mock
    private VoteTopicService voteTopicService;

    @Mock
    private VoteOptionService voteOptionService;

    @Mock
    private VoteRecordService voteRecordService;

    @InjectMocks
    private VoteResultServiceImpl voteResultService;

    private VoteTopic testTopic;
    private List<VoteOption> testOptions;
    private List<VoteRecord> testRecords;

    @BeforeEach
    void setUp() {
        // 创建测试投票主题
        testTopic = new VoteTopic();
        testTopic.setId(1L);
        testTopic.setTitle("测试投票议题");
        testTopic.setIsAreaWeighted(true);
        testTopic.setIsRealName(true);
        testTopic.setIsResultPublic(true);
        testTopic.setStatus("ACTIVE");

        // 创建测试选项
        VoteOption option1 = new VoteOption();
        option1.setId(1L);
        option1.setOptionText("同意");
        option1.setTopic(testTopic);

        VoteOption option2 = new VoteOption();
        option2.setId(2L);
        option2.setOptionText("反对");
        option2.setTopic(testTopic);

        VoteOption option3 = new VoteOption();
        option3.setId(3L);
        option3.setOptionText("弃权");
        option3.setTopic(testTopic);

        testOptions = Arrays.asList(option1, option2, option3);

        // 创建测试投票记录
        testRecords = new ArrayList<>();
        
        // 10票同意，总权重1000
        for (int i = 0; i < 10; i++) {
            VoteRecord record = new VoteRecord();
            record.setId((long)(i + 1));
            record.setTopic(testTopic);
            record.setOption(option1);
            record.setVoteWeight(BigDecimal.valueOf(100));
            testRecords.add(record);
        }
        
        // 5票反对，总权重300
        for (int i = 0; i < 5; i++) {
            VoteRecord record = new VoteRecord();
            record.setId((long)(i + 11));
            record.setTopic(testTopic);
            record.setOption(option2);
            record.setVoteWeight(BigDecimal.valueOf(60));
            testRecords.add(record);
        }
        
        // 2票弃权，总权重150
        for (int i = 0; i < 2; i++) {
            VoteRecord record = new VoteRecord();
            record.setId((long)(i + 16));
            record.setTopic(testTopic);
            record.setOption(option3);
            record.setVoteWeight(BigDecimal.valueOf(75));
            testRecords.add(record);
        }
    }

    @Test
    void testGetVoteResult() {
        when(voteTopicService.findById(1L)).thenReturn(testTopic);
        when(voteOptionService.findByTopicId(1L)).thenReturn(testOptions);
        when(voteRecordService.findByTopicId(1L)).thenReturn(testRecords);

        Map<String, Object> result = voteResultService.getVoteResult(1L);

        assertNotNull(result);
        assertEquals(1L, result.get("topicId"));
        assertEquals("测试投票议题", result.get("topicTitle"));
        assertEquals(17, result.get("totalVotes"));
        
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> options = (List<Map<String, Object>>) result.get("options");
        assertEquals(3, options.size());
        
        // 验证第一个选项（同意）的统计数据
        Map<String, Object> option1Result = options.get(0);
        assertEquals(1L, option1Result.get("optionId"));
        assertEquals("同意", option1Result.get("optionText"));
        assertEquals(10, option1Result.get("voteCount"));
        assertEquals(new BigDecimal("1000"), option1Result.get("voteWeight"));
        
        // 验证第二个选项（反对）的统计数据
        Map<String, Object> option2Result = options.get(1);
        assertEquals(2L, option2Result.get("optionId"));
        assertEquals("反对", option2Result.get("optionText"));
        assertEquals(5, option2Result.get("voteCount"));
        assertEquals(new BigDecimal("300"), option2Result.get("voteWeight"));
        
        // 验证第三个选项（弃权）的统计数据
        Map<String, Object> option3Result = options.get(2);
        assertEquals(3L, option3Result.get("optionId"));
        assertEquals("弃权", option3Result.get("optionText"));
        assertEquals(2, option3Result.get("voteCount"));
        assertEquals(new BigDecimal("150"), option3Result.get("voteWeight"));
    }

    @Test
    void testGetVoteOptionResults() {
        when(voteOptionService.findByTopicId(1L)).thenReturn(testOptions);
        when(voteRecordService.findByTopicId(1L)).thenReturn(testRecords);

        List<Map<String, Object>> results = voteResultService.getVoteOptionResults(1L);

        assertNotNull(results);
        assertEquals(3, results.size());
        
        // 验证百分比计算
        Map<String, Object> option1 = results.get(0);
        assertEquals(10, option1.get("voteCount"));
        assertEquals(new BigDecimal("1000"), option1.get("voteWeight"));
        assertEquals(58.82, (Double)option1.get("countPercent"), 0.05);
        assertEquals(69.0, (Double)option1.get("weightPercent"), 0.05);
        
        Map<String, Object> option2 = results.get(1);
        assertEquals(5, option2.get("voteCount"));
        assertEquals(new BigDecimal("300"), option2.get("voteWeight"));
        assertEquals(29.41, (Double)option2.get("countPercent"), 0.05);
        assertEquals(20.7, (Double)option2.get("weightPercent"), 0.05);
        
        Map<String, Object> option3 = results.get(2);
        assertEquals(2, option3.get("voteCount"));
        assertEquals(new BigDecimal("150"), option3.get("voteWeight"));
        assertEquals(11.76, (Double)option3.get("countPercent"), 0.05);
        assertEquals(10.3, (Double)option3.get("weightPercent"), 0.05);
    }

    @Test
    void testGetOptionSummary() {
        VoteOption testOption = testOptions.get(0); // 同意选项
        when(voteOptionService.findById(1L)).thenReturn(testOption);
        when(voteRecordService.findByTopicId(1L)).thenReturn(testRecords);

        Map<String, Object> summary = voteResultService.getOptionSummary(1L);

        assertNotNull(summary);
        assertEquals(1L, summary.get("optionId"));
        assertEquals(10, summary.get("voteCount"));
        assertEquals(new BigDecimal("1000"), summary.get("voteWeight"));
    }

    @Test
    void testCountTotalVotes() {
        when(voteRecordService.findByTopicId(1L)).thenReturn(testRecords);

        int totalVotes = voteResultService.countTotalVotes(1L);

        assertEquals(17, totalVotes);
    }

    @Test
    void testCalculateTotalWeight() {
        when(voteRecordService.findByTopicId(1L)).thenReturn(testRecords);

        BigDecimal totalWeight = voteResultService.calculateTotalWeight(1L);

        assertEquals(new BigDecimal("1450"), totalWeight);
    }
} 