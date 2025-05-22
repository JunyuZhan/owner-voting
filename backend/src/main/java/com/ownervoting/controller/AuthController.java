package com.ownervoting.controller;

import com.ownervoting.model.dto.AuthRequest;
import com.ownervoting.model.vo.ApiResponse;
import com.ownervoting.model.vo.AuthResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.servlet.http.HttpSession;
import java.util.Random;
import com.ownervoting.service.SmsService;
import com.ownervoting.service.OwnerService;
import com.ownervoting.model.entity.Owner;
import com.ownervoting.security.JwtUtil;
import com.ownervoting.service.AdminUserService;
import com.ownervoting.model.entity.AdminUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private SmsService smsService;
    @Autowired
    private HttpSession session;
    @Autowired
    private OwnerService ownerService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AdminUserService adminUserService;

    @PostMapping("/sms-code")
    public ApiResponse<?> sendSmsCode(@RequestBody AuthRequest request) {
        String phone = request.getPhone();
        if (phone == null || phone.isEmpty()) {
            return ApiResponse.error(400, "手机号不能为空");
        }
        // 生成6位验证码
        String code = String.format("%06d", new Random().nextInt(1000000));
        boolean sent = smsService.sendSmsCode(phone, code);
        if (sent) {
            // 简单存 session，实际生产建议用 Redis 并设置过期时间
            session.setAttribute("SMS_CODE_" + phone, code);
            return ApiResponse.success("验证码已发送");
        } else {
            return ApiResponse.error(500, "验证码发送失败");
        }
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@RequestBody AuthRequest request) {
        String phone = request.getPhone();
        String code = request.getCode();
        if (phone == null || phone.isEmpty() || code == null || code.isEmpty()) {
            return ApiResponse.error(400, "手机号和验证码不能为空");
        }
        String sessionCode = (String) session.getAttribute("SMS_CODE_" + phone);
        if (sessionCode == null || !sessionCode.equals(code)) {
            return ApiResponse.error(401, "验证码错误或已过期");
        }
        Owner owner = ownerService.findByPhone(phone);
        if (owner == null) {
            return ApiResponse.error(404, "用户不存在");
        }
        // 登录成功，生成token
        String token = jwtUtil.generateToken(phone, "");
        AuthResponse resp = new AuthResponse();
        resp.setToken(token);
        resp.setExpiresIn(86400L); // 1天
        resp.setUserInfo(owner);
        // 清除验证码
        session.removeAttribute("SMS_CODE_" + phone);
        return ApiResponse.success(resp);
    }

    @PostMapping("/admin/login")
    public ApiResponse<AuthResponse> adminLogin(@RequestBody AuthRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return ApiResponse.error(400, "用户名和密码不能为空");
        }
        AdminUser admin = adminUserService.findByUsername(username);
        if (admin == null) {
            return ApiResponse.error(404, "管理员不存在");
        }
        if (!password.equals(admin.getPasswordHash())) { // 实际生产应加密比对
            return ApiResponse.error(401, "密码错误");
        }
        if (admin.getIsActive() == null || !admin.getIsActive()) {
            return ApiResponse.error(403, "账号已禁用");
        }
        // 登录成功，生成token
        String token = jwtUtil.generateToken(username, admin.getRole() != null ? admin.getRole().name() : "");
        AuthResponse resp = new AuthResponse();
        resp.setToken(token);
        resp.setExpiresIn(86400L); // 1天
        resp.setUserInfo(admin);
        return ApiResponse.success(resp);
    }

    @GetMapping("/me")
    public ApiResponse<?> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return ApiResponse.error(401, "未登录");
        }
        String username = authentication.getPrincipal().toString();
        // 先查业主
        Owner owner = ownerService.findByPhone(username);
        if (owner != null) {
            return ApiResponse.success(owner);
        }
        // 再查管理员
        AdminUser admin = adminUserService.findByUsername(username);
        if (admin != null) {
            return ApiResponse.success(admin);
        }
        return ApiResponse.error(404, "用户不存在");
    }
} 