package com.ownervoting.service.impl;

import com.ownervoting.service.SmsService;
import org.springframework.stereotype.Service;

@Service
public class SmsServiceImpl implements SmsService {
    @Override
    public boolean sendSmsCode(String phone, String code) {
        // 模拟短信发送，实际可对接第三方短信平台
        System.out.printf("[模拟短信] 向 %s 发送验证码：%s\n", phone, code);
        return true;
    }
} 