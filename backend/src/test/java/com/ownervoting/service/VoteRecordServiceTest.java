package com.ownervoting.service;

import com.ownervoting.exception.BusinessException;
import com.ownervoting.model.entity.House;
import com.ownervoting.model.entity.Owner;
import com.ownervoting.model.entity.VoteOption;
import com.ownervoting.model.entity.VoteRecord;
import com.ownervoting.model.entity.VoteTopic;
import com.ownervoting.repository.VoteRecordRepository;
import com.ownervoting.service.impl.VoteRecordServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class VoteRecordServiceTest {

    @Mock
    private VoteRecordRepository voteRecordRepository;

    @Mock
    private SystemLogService systemLogService;

    @InjectMocks
    private VoteRecordServiceImpl voteRecordService;

    private VoteRecord sampleVoteRecord;
    private VoteTopic sampleTopic;
    private Owner sampleOwner;
    private House sampleHouse;
    private VoteOption sampleOption;

    @BeforeEach
    void setUp() {
        // 创建测试数据
        sampleTopic = new VoteTopic();
        sampleTopic.setId(1L);
        sampleTopic.setTitle("测试投票");

        sampleOwner = new Owner();
        sampleOwner.setId(1L);
        sampleOwner.setName("张三");

        sampleHouse = new House();
        sampleHouse.setId(1L);
        sampleHouse.setArea(BigDecimal.valueOf(100.0));

        sampleOption = new VoteOption();
        sampleOption.setId(1L);
        sampleOption.setOptionText("同意");
        sampleOption.setTopic(sampleTopic);

        sampleVoteRecord = new VoteRecord();
        sampleVoteRecord.setId(1L);
        sampleVoteRecord.setTopic(sampleTopic);
        sampleVoteRecord.setVoter(sampleOwner);
        sampleVoteRecord.setHouse(sampleHouse);
        sampleVoteRecord.setOption(sampleOption);
        sampleVoteRecord.setVoteWeight(BigDecimal.valueOf(100.0));
    }

    @Test
    void testFindById() {
        when(voteRecordRepository.findById(1L)).thenReturn(Optional.of(sampleVoteRecord));

        VoteRecord found = voteRecordService.findById(1L);
        assertNotNull(found);
        assertEquals(1L, found.getId());
        assertEquals("张三", found.getVoter().getName());
        assertEquals("测试投票", found.getTopic().getTitle());
    }

    @Test
    void testFindByTopicId() {
        List<VoteRecord> records = new ArrayList<>();
        records.add(sampleVoteRecord);

        when(voteRecordRepository.findByTopicId(1L)).thenReturn(records);

        List<VoteRecord> found = voteRecordService.findByTopicId(1L);
        assertNotNull(found);
        assertEquals(1, found.size());
        assertEquals(1L, found.get(0).getId());
    }

    @Test
    void testFindByOwnerId() {
        List<VoteRecord> records = new ArrayList<>();
        records.add(sampleVoteRecord);

        when(voteRecordRepository.findByVoterId(1L)).thenReturn(records);

        List<VoteRecord> found = voteRecordService.findByOwnerId(1L);
        assertNotNull(found);
        assertEquals(1, found.size());
        assertEquals(1L, found.get(0).getId());
    }

    @Test
    void testAddVoteRecord_Success() {
        // 模拟没有已存在的投票记录
        when(voteRecordRepository.findByTopicIdAndVoterIdAndHouseId(1L, 1L, 1L))
                .thenReturn(new ArrayList<>());
        
        when(voteRecordRepository.save(any(VoteRecord.class))).thenReturn(sampleVoteRecord);
        
        VoteRecord saved = voteRecordService.addVoteRecord(sampleVoteRecord);
        
        assertNotNull(saved);
        assertEquals(1L, saved.getId());
        verify(systemLogService, times(1)).addLog(any());
    }
    
    @Test
    void testAddVoteRecord_AlreadyVoted() {
        // 模拟已经存在投票记录
        List<VoteRecord> existingRecords = new ArrayList<>();
        existingRecords.add(sampleVoteRecord);
        
        when(voteRecordRepository.findByTopicIdAndVoterIdAndHouseId(1L, 1L, 1L))
                .thenReturn(existingRecords);
        
        assertThrows(BusinessException.class, () -> {
            voteRecordService.addVoteRecord(sampleVoteRecord);
        });
        
        verify(voteRecordRepository, never()).save(any(VoteRecord.class));
    }
    
    @Test
    void testFindPage() {
        List<VoteRecord> records = new ArrayList<>();
        records.add(sampleVoteRecord);
        Page<VoteRecord> page = new PageImpl<>(records);
        
        when(voteRecordRepository.findAll(any(PageRequest.class))).thenReturn(page);
        
        Page<VoteRecord> result = voteRecordService.findPage(PageRequest.of(0, 10));
        
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
    }
} 