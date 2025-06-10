package com.ownervoting.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * OCR识别服务接口
 * 负责身份证、房产证等证件的OCR识别
 */
public interface OcrService {
    
    /**
     * 识别身份证信息
     * @param file 身份证图片文件
     * @param isBack 是否为身份证背面
     * @return OCR识别结果
     */
    OcrResult recognizeIdCard(MultipartFile file, boolean isBack);
    
    /**
     * 识别房产证信息
     * @param file 房产证图片文件
     * @return OCR识别结果
     */
    OcrResult recognizeHouseCert(MultipartFile file);
    
    /**
     * 通用文本识别
     * @param file 图片文件
     * @return OCR识别结果
     */
    OcrResult recognizeText(MultipartFile file);
    
    /**
     * OCR识别结果
     */
    class OcrResult {
        private boolean success;
        private String text;
        private String name;        // 姓名
        private String idNumber;    // 身份证号
        private String address;     // 地址
        private String issueDate;   // 签发日期
        private String expiryDate;  // 有效期
        private String houseNumber; // 房产证号
        private String houseAddress; // 房产地址
        private String owner;       // 房产所有人
        private String error;
        
        // 构造函数
        public OcrResult() {}
        
        public OcrResult(boolean success, String text) {
            this.success = success;
            this.text = text;
        }
        
        public static OcrResult success(String text) {
            return new OcrResult(true, text);
        }
        
        public static OcrResult error(String error) {
            OcrResult result = new OcrResult(false, null);
            result.error = error;
            return result;
        }
        
        // Getters and Setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public String getText() { return text; }
        public void setText(String text) { this.text = text; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getIdNumber() { return idNumber; }
        public void setIdNumber(String idNumber) { this.idNumber = idNumber; }
        
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        
        public String getIssueDate() { return issueDate; }
        public void setIssueDate(String issueDate) { this.issueDate = issueDate; }
        
        public String getExpiryDate() { return expiryDate; }
        public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }
        
        public String getHouseNumber() { return houseNumber; }
        public void setHouseNumber(String houseNumber) { this.houseNumber = houseNumber; }
        
        public String getHouseAddress() { return houseAddress; }
        public void setHouseAddress(String houseAddress) { this.houseAddress = houseAddress; }
        
        public String getOwner() { return owner; }
        public void setOwner(String owner) { this.owner = owner; }
        
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
    }
}