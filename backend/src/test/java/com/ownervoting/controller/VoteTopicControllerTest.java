package com.ownervoting.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ownervoting.model.dto.VoteTopicAddDTO;
import com.ownervoting.model.entity.VoteTopic;
import com.ownervoting.model.vo.VoteTopicVO;
import com.ownervoting.service.MonitorService;
import com.ownervoting.service.VoteOptionService;
import com.ownervoting.service.VoteRecordService;
import com.ownervoting.service.VoteResultService;
import com.ownervoting.service.VoteTopicService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = VoteTopicController.class)
@ActiveProfiles("test")
public class VoteTopicControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VoteTopicService voteTopicService;

    @MockBean
    private VoteRecordService voteRecordService;

    @MockBean
    private VoteOptionService voteOptionService;

    @MockBean
    private VoteResultService voteResultService;
    
    @MockBean
    private MonitorService monitorService;

    private VoteTopic testTopic;
    private List<VoteTopic> testTopics;

    @BeforeEach
    void setUp() {
        testTopic = new VoteTopic();
        testTopic.setId(1L);
        testTopic.setTitle("测试投票");
        testTopic.setDescription("这是一个测试投票");
        testTopic.setStartTime(LocalDateTime.now());
        testTopic.setEndTime(LocalDateTime.now().plusDays(7));
        testTopic.setIsAreaWeighted(true);
        testTopic.setIsRealName(true);
        testTopic.setIsResultPublic(true);
        testTopic.setStatus("ACTIVE");

        VoteTopic topic2 = new VoteTopic();
        topic2.setId(2L);
        topic2.setTitle("第二个测试投票");
        topic2.setStatus("PENDING");

        testTopics = Arrays.asList(testTopic, topic2);
    }

    @Test
    @WithMockUser(roles = "SYSTEM_ADMIN")
    void testAddVoteTopic() throws Exception {
        VoteTopicAddDTO dto = new VoteTopicAddDTO();
        dto.setTitle("新投票");
        dto.setDescription("描述");
        dto.setCommunityId(1L);
        dto.setStartTime(LocalDateTime.now());
        dto.setEndTime(LocalDateTime.now().plusDays(7));
        dto.setIsAreaWeighted(true);
        dto.setIsRealName(true);
        dto.setIsResultPublic(true);
        dto.setStatus("DRAFT");

        when(voteTopicService.addTopic(any(VoteTopic.class))).thenReturn(testTopic);

        mockMvc.perform(post("/api/vote-topic/add")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.title").value("测试投票"));

        verify(voteTopicService, times(1)).addTopic(any(VoteTopic.class));
    }

    @Test
    @WithMockUser(roles = "COMMUNITY_ADMIN")
    void testDeleteVoteTopic() throws Exception {
        mockMvc.perform(delete("/api/vote-topic/delete/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        verify(voteTopicService, times(1)).deleteTopic(1L);
    }

    @Test
    @WithMockUser
    void testGetById() throws Exception {
        when(voteTopicService.findById(1L)).thenReturn(testTopic);

        mockMvc.perform(get("/api/vote-topic/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1L))
                .andExpect(jsonPath("$.data.title").value("测试投票"));

        verify(voteTopicService, times(1)).findById(1L);
    }

    @Test
    @WithMockUser
    void testGetAll() throws Exception {
        when(voteTopicService.findAll()).thenReturn(testTopics);

        mockMvc.perform(get("/api/vote-topic/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(2)))
                .andExpect(jsonPath("$.data[0].id").value(1L))
                .andExpect(jsonPath("$.data[1].id").value(2L));

        verify(voteTopicService, times(1)).findAll();
    }

    @Test
    @WithMockUser
    void testGetVoteList() throws Exception {
        when(voteTopicService.findAll()).thenReturn(testTopics);

        mockMvc.perform(get("/api/vote-topic/votes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(2)));

        verify(voteTopicService, times(1)).findAll();
    }

    @Test
    @WithMockUser
    void testGetVoteResult() throws Exception {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("topicId", 1L);
        resultMap.put("totalVotes", 20);
        resultMap.put("options", Arrays.asList(
                Map.of("optionId", 1L, "optionText", "同意", "voteCount", 15),
                Map.of("optionId", 2L, "optionText", "反对", "voteCount", 5)
        ));

        when(voteResultService.getVoteResult(1L)).thenReturn(resultMap);

        mockMvc.perform(get("/api/vote-topic/votes/1/result"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.topicId").value(1))
                .andExpect(jsonPath("$.data.totalVotes").value(20))
                .andExpect(jsonPath("$.data.options", hasSize(2)))
                .andExpect(jsonPath("$.data.options[0].optionText").value("同意"))
                .andExpect(jsonPath("$.data.options[0].voteCount").value(15));

        verify(voteResultService, times(1)).getVoteResult(1L);
    }

    @Test
    @WithMockUser
    void testGetPage() throws Exception {
        Page<VoteTopic> page = new PageImpl<>(testTopics, PageRequest.of(0, 10), 2);
        when(voteTopicService.findPage(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/vote-topic/page?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(2))
                .andExpect(jsonPath("$.data.list", hasSize(2)))
                .andExpect(jsonPath("$.data.list[0].id").value(1L));

        verify(voteTopicService, times(1)).findPage(any(PageRequest.class));
    }
} 