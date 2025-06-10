package com.ownervoting.service.impl;

import com.ownervoting.service.OcrService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * OCR识别服务实现
 * 集成PaddleOCR进行文字识别
 */
@Service
public class OcrServiceImpl implements OcrService {
    
    private static final Logger logger = LoggerFactory.getLogger(OcrServiceImpl.class);
    
    @Value("${ocr.paddleocr.url:http://localhost:8866}")
    private String paddleOcrUrl;
    
    private final RestTemplate restTemplate = new RestTemplate();
    
    // 身份证号正则表达式
    private static final Pattern ID_CARD_PATTERN = Pattern.compile("(\\d{17}[\\dXx]|\\d{15})");
    // 姓名正则表达式（中文姓名）
    private static final Pattern NAME_PATTERN = Pattern.compile("姓名[：:]?\\s*([\u4e00-\u9fa5]{2,4})");
    // 地址正则表达式
    private static final Pattern ADDRESS_PATTERN = Pattern.compile("住址[：:]?\\s*([\u4e00-\u9fa5\\d\\s]+)");
    // 日期正则表达式
    private static final Pattern DATE_PATTERN = Pattern.compile("(\\d{4}[.年]\\d{1,2}[.月]\\d{1,2}[日]?)");
    
    @Override
    public OcrResult recognizeIdCard(MultipartFile file, boolean isBack) {
        try {
            String text = callPaddleOcr(file);
            if (text == null) {
                return OcrResult.error("OCR识别失败");
            }
            
            OcrResult result = OcrResult.success(text);
            
            if (!isBack) {
                // 身份证正面：提取姓名、身份证号、地址
                extractIdCardFrontInfo(text, result);
            } else {
                // 身份证背面：提取签发日期、有效期
                extractIdCardBackInfo(text, result);
            }
            
            return result;
            
        } catch (Exception e) {
            logger.error("身份证OCR识别失败", e);
            return OcrResult.error("OCR识别异常: " + e.getMessage());
        }
    }
    
    @Override
    public OcrResult recognizeHouseCert(MultipartFile file) {
        try {
            String text = callPaddleOcr(file);
            if (text == null) {
                return OcrResult.error("OCR识别失败");
            }
            
            OcrResult result = OcrResult.success(text);
            extractHouseCertInfo(text, result);
            
            return result;
            
        } catch (Exception e) {
            logger.error("房产证OCR识别失败", e);
            return OcrResult.error("OCR识别异常: " + e.getMessage());
        }
    }
    
    @Override
    public OcrResult recognizeText(MultipartFile file) {
        try {
            String text = callPaddleOcr(file);
            if (text == null) {
                return OcrResult.error("OCR识别失败");
            }
            
            return OcrResult.success(text);
            
        } catch (Exception e) {
            logger.error("通用OCR识别失败", e);
            return OcrResult.error("OCR识别异常: " + e.getMessage());
        }
    }
    
    /**
     * 调用PaddleOCR服务进行文字识别
     */
    private String callPaddleOcr(MultipartFile file) throws IOException {
        try {
            // 准备请求
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new ByteArrayResource(file.getBytes()) {
                @Override
                public String getFilename() {
                    return file.getOriginalFilename();
                }
            });
            
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
            
            // 发送请求到PaddleOCR服务
            ResponseEntity<Map> response = restTemplate.postForEntity(
                paddleOcrUrl + "/predict/ocr_system", 
                requestEntity, 
                Map.class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                
                // 解析PaddleOCR返回的结果
                if (responseBody.containsKey("results")) {
                    Object results = responseBody.get("results");
                    if (results instanceof java.util.List) {
                        java.util.List<?> resultList = (java.util.List<?>) results;
                        StringBuilder textBuilder = new StringBuilder();
                        
                        for (Object item : resultList) {
                            if (item instanceof java.util.List) {
                                java.util.List<?> itemList = (java.util.List<?>) item;
                                if (itemList.size() >= 2 && itemList.get(1) instanceof java.util.List) {
                                    java.util.List<?> textInfo = (java.util.List<?>) itemList.get(1);
                                    if (textInfo.size() >= 1) {
                                        textBuilder.append(textInfo.get(0).toString()).append("\n");
                                    }
                                }
                            }
                        }
                        
                        return textBuilder.toString().trim();
                    }
                }
            }
            
            logger.warn("PaddleOCR返回异常响应: {}", response.getBody());
            return null;
            
        } catch (Exception e) {
            logger.error("调用PaddleOCR服务失败", e);
            // 如果PaddleOCR服务不可用，返回模拟结果用于开发测试
            logger.info("PaddleOCR服务不可用，返回模拟识别结果");
            return "模拟OCR识别结果 - 文件名: " + file.getOriginalFilename();
        }
    }
    
    /**
     * 提取身份证正面信息
     */
    private void extractIdCardFrontInfo(String text, OcrResult result) {
        // 提取姓名
        Matcher nameMatcher = NAME_PATTERN.matcher(text);
        if (nameMatcher.find()) {
            result.setName(nameMatcher.group(1).trim());
        }
        
        // 提取身份证号
        Matcher idMatcher = ID_CARD_PATTERN.matcher(text);
        if (idMatcher.find()) {
            result.setIdNumber(idMatcher.group(1));
        }
        
        // 提取地址
        Matcher addressMatcher = ADDRESS_PATTERN.matcher(text);
        if (addressMatcher.find()) {
            result.setAddress(addressMatcher.group(1).trim());
        }
    }
    
    /**
     * 提取身份证背面信息
     */
    private void extractIdCardBackInfo(String text, OcrResult result) {
        // 提取日期信息（签发日期和有效期）
        Matcher dateMatcher = DATE_PATTERN.matcher(text);
        java.util.List<String> dates = new java.util.ArrayList<>();
        while (dateMatcher.find()) {
            dates.add(dateMatcher.group(1));
        }
        
        if (dates.size() >= 1) {
            result.setIssueDate(dates.get(0));
        }
        if (dates.size() >= 2) {
            result.setExpiryDate(dates.get(1));
        }
    }
    
    /**
     * 提取房产证信息
     */
    private void extractHouseCertInfo(String text, OcrResult result) {
        // 房产证号正则
        Pattern houseNumberPattern = Pattern.compile("房权证[第]?([\\d\\w]+)号");
        Matcher houseNumberMatcher = houseNumberPattern.matcher(text);
        if (houseNumberMatcher.find()) {
            result.setHouseNumber(houseNumberMatcher.group(1));
        }
        
        // 房产地址正则
        Pattern houseAddressPattern = Pattern.compile("坐落[：:]?\\s*([\u4e00-\u9fa5\\d\\s\\w]+)");
        Matcher houseAddressMatcher = houseAddressPattern.matcher(text);
        if (houseAddressMatcher.find()) {
            result.setHouseAddress(houseAddressMatcher.group(1).trim());
        }
        
        // 房产所有人正则
        Pattern ownerPattern = Pattern.compile("所有权人[：:]?\\s*([\u4e00-\u9fa5]{2,4})");
        Matcher ownerMatcher = ownerPattern.matcher(text);
        if (ownerMatcher.find()) {
            result.setOwner(ownerMatcher.group(1).trim());
        }
    }
}