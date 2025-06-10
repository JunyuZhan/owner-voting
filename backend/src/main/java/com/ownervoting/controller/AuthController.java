package com.ownervoting.controller;

import java.security.SecureRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ownervoting.model.dto.AdminUserDTO;
import com.ownervoting.model.dto.AuthRequest;
import com.ownervoting.model.dto.ChangePasswordDTO;
import com.ownervoting.model.dto.UpdateProfileDTO;
import com.ownervoting.model.entity.AdminUser;
import com.ownervoting.model.entity.Owner;
import com.ownervoting.model.vo.ApiResponse;
import com.ownervoting.model.vo.AuthResponse;
import com.ownervoting.security.JwtUtil;
import com.ownervoting.service.AdminUserService;
import com.ownervoting.service.OwnerService;
import com.ownervoting.service.SmsService;

import jakarta.servlet.http.HttpSession;

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
        
        // 检查发送频率限制（防止短信轰炸）
        String lastSendTimeKey = "SMS_LAST_SEND_" + phone;
        Long lastSendTime = (Long) session.getAttribute(lastSendTimeKey);
        long currentTime = System.currentTimeMillis();
        if (lastSendTime != null && (currentTime - lastSendTime) < 60000) { // 60秒限制
            return ApiResponse.error(429, "验证码发送过于频繁，请稍后再试");
        }
        
        // 生成更安全的6位数字验证码（使用SecureRandom）
        SecureRandom secureRandom = new SecureRandom();
        String code = String.format("%06d", secureRandom.nextInt(1000000));
        
        boolean sent = smsService.sendSmsCode(phone, code);
        if (sent) {
            // 存储验证码和发送时间
            session.setAttribute("SMS_CODE_" + phone, code);
            session.setAttribute("SMS_CODE_TIME_" + phone, currentTime);
            session.setAttribute(lastSendTimeKey, currentTime);
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
        Long codeTime = (Long) session.getAttribute("SMS_CODE_TIME_" + phone);
        
        // 检查验证码是否存在
        if (sessionCode == null || !sessionCode.equals(code)) {
            return ApiResponse.error(401, "验证码错误或已过期");
        }
        
        // 检查验证码是否过期（5分钟有效期）
        if (codeTime == null || (System.currentTimeMillis() - codeTime) > 300000) {
            // 清除过期的验证码
            session.removeAttribute("SMS_CODE_" + phone);
            session.removeAttribute("SMS_CODE_TIME_" + phone);
            return ApiResponse.error(401, "验证码已过期，请重新获取");
        }
        
        // 验证成功后立即清除验证码（防止重复使用）
        session.removeAttribute("SMS_CODE_" + phone);
        session.removeAttribute("SMS_CODE_TIME_" + phone);
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
        System.out.println("Admin登录请求 - 用户名: " + username); // 添加调试日志
        
        if (username == null || username.isEmpty() || password == null || password.isEmpty()) {
            return ApiResponse.error(400, "用户名和密码不能为空");
        }
        AdminUser admin = adminUserService.findByUsernameWithCommunity(username);
        if (admin == null) {
            System.out.println("管理员不存在: " + username); // 添加调试日志
            return ApiResponse.error(404, "管理员不存在");
        }
        if (!password.equals(admin.getPasswordHash())) { // 实际生产应加密比对
            System.out.println("密码错误 - 输入: " + password + ", 存储: " + admin.getPasswordHash()); // 添加调试日志
            return ApiResponse.error(401, "密码错误");
        }
        if (admin.getEnabled() == null || !admin.getEnabled()) {
            System.out.println("账号已禁用: " + username); // 添加调试日志
            return ApiResponse.error(403, "账号已禁用");
        }
        
        // 登录成功，生成token
        String role = admin.getRole() != null ? admin.getRole().name() : "";
        System.out.println("生成Token - 用户: " + username + ", 角色: " + role); // 添加调试日志
        String token = jwtUtil.generateToken(username, role);
        System.out.println("生成的Token: " + token.substring(0, Math.min(50, token.length())) + "..."); // 添加调试日志
        
        AuthResponse resp = new AuthResponse();
        resp.setToken(token);
        resp.setExpiresIn(86400L); // 1天
        resp.setUserInfo(AdminUserDTO.fromEntity(admin)); // 使用DTO避免序列化问题
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
        AdminUser admin = adminUserService.findByUsernameWithCommunity(username);
        if (admin != null) {
            return ApiResponse.success(AdminUserDTO.fromEntity(admin)); // 使用DTO避免序列化问题
        }
        return ApiResponse.error(404, "用户不存在");
    }

    @PostMapping("/admin/change-password")
    public ApiResponse<?> changeAdminPassword(@RequestBody ChangePasswordDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return ApiResponse.error(401, "未登录");
        }
        
        String username = authentication.getPrincipal().toString();
        AdminUser admin = adminUserService.findByUsername(username);
        if (admin == null) {
            return ApiResponse.error(404, "管理员不存在");
        }
        
        // 验证原密码（实际生产应使用加密比对）
        if (!request.getOldPassword().equals(admin.getPasswordHash())) {
            return ApiResponse.error(400, "原密码错误");
        }
        
        // 更新密码（实际生产应加密存储）
        admin.setPasswordHash(request.getNewPassword());
        adminUserService.addAdminUser(admin);
        
        return ApiResponse.success("密码修改成功");
    }

    @PutMapping("/admin/profile")
    public ApiResponse<?> updateAdminProfile(@RequestBody UpdateProfileDTO request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return ApiResponse.error(401, "未登录");
        }
        
        String username = authentication.getPrincipal().toString();
        AdminUser admin = adminUserService.findByUsername(username);
        if (admin == null) {
            return ApiResponse.error(404, "管理员不存在");
        }
        
        // 更新个人信息
        if (request.getName() != null && !request.getName().trim().isEmpty()) {
            admin.setName(request.getName().trim());
        }
        if (request.getPhone() != null && !request.getPhone().trim().isEmpty()) {
            admin.setPhone(request.getPhone().trim());
        }
        if (request.getEmail() != null && !request.getEmail().trim().isEmpty()) {
            admin.setEmail(request.getEmail().trim());
        }
        
        adminUserService.addAdminUser(admin);
        
        return ApiResponse.success("个人信息更新成功");
    }
}