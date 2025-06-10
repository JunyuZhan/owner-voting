package com.ownervoting.service;

import com.ownervoting.model.entity.VoteTopic;
import org.springframework.http.ResponseEntity;

import java.util.Map;

/**
 * PDF导出服务接口
 * 负责生成各种PDF报告和文档
 */
public interface PdfExportService {
    
    /**
     * 导出投票结果PDF
     * @param topicId 投票议题ID
     * @return PDF文件的字节数组
     */
    byte[] exportVoteResult(Long topicId);
    
    /**
     * 导出投票详情PDF
     * @param topicId 投票议题ID
     * @return PDF文件的字节数组
     */
    byte[] exportVoteDetail(Long topicId);
    
    /**
     * 导出业主认证报告PDF
     * @param communityId 社区ID
     * @return PDF文件的字节数组
     */
    byte[] exportOwnerVerificationReport(Long communityId);
    
    /**
     * 导出系统统计报告PDF
     * @param communityId 社区ID
     * @return PDF文件的字节数组
     */
    byte[] exportSystemReport(Long communityId);
    
    /**
     * 生成投票结果下载响应
     * @param topicId 投票议题ID
     * @return HTTP响应实体
     */
    ResponseEntity<byte[]> downloadVoteResult(Long topicId);
}