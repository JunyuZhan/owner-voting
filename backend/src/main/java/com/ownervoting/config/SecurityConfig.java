package com.ownervoting.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.ownervoting.filter.XssFilter;
import com.ownervoting.security.JwtAuthorizationFilter;
import com.ownervoting.security.TenantFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthorizationFilter jwtAuthorizationFilter;
    
    @Autowired
    private XssFilter xssFilter;
    
    @Autowired
    private TenantFilter tenantFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用基本认证
            .httpBasic(httpBasic -> httpBasic.disable())
            // 禁用表单登录
            .formLogin(formLogin -> formLogin.disable())
            // 禁用登出
            .logout(logout -> logout.disable())
            // 禁用匿名登录
            .anonymous(anonymous -> anonymous.disable())
            // 启用CORS
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // 完全禁用CSRF，因为使用JWT认证的REST API不需要CSRF保护
            .csrf(csrf -> csrf.disable())
            // 配置会话管理
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 配置安全头
            .headers(headers -> headers
                .contentSecurityPolicy(csp -> csp.policyDirectives("default-src 'self'; script-src 'self'; style-src 'self'; img-src 'self' data:;"))
                .referrerPolicy(referrer -> referrer.policy(ReferrerPolicyHeaderWriter.ReferrerPolicy.SAME_ORIGIN))
                .frameOptions(frame -> frame.deny())
                .xssProtection(xss -> xss.headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
                .contentTypeOptions(contentType -> contentType.disable()))
            // 请求授权配置
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(
                    "/api/v1/auth/login",
                    "/api/v1/auth/admin/login", 
                    "/api/v1/auth/refresh",
                    "/api/admin/login",
                    "/api/error/**",
                    "/api/community/public",
                    "/api/announcement/public",
                    "/api/vote-topic/public",
                    "/api/ad/current",
                    "/api/ad/test",
                    "/api/ad/click/**",
                    "/swagger-ui.html",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                .requestMatchers("/api/admin/**", "/api/admin-user/**").hasAnyRole("SYSTEM_ADMIN", "COMMUNITY_ADMIN", "OPERATOR")
                .requestMatchers("/api/monitor/**").hasRole("SYSTEM_ADMIN")
                // 广告管理权限（除了公开的current和click接口）
                .requestMatchers("/api/ad/list", "/api/ad/create", "/api/ad/update/**", "/api/ad/delete/**", "/api/ad/toggle/**", "/api/ad/detail/**").hasAnyRole("SYSTEM_ADMIN", "COMMUNITY_ADMIN", "OPERATOR")
                // 需要管理员权限的操作
                .requestMatchers("/api/announcement/add", "/api/announcement/delete/**").hasAnyRole("SYSTEM_ADMIN", "COMMUNITY_ADMIN", "OPERATOR")
                .requestMatchers("/api/vote-topic/add", "/api/vote-topic/delete/**").hasAnyRole("SYSTEM_ADMIN", "COMMUNITY_ADMIN", "OPERATOR")
                // 其他需要认证的请求
                .anyRequest().authenticated()
            )
            // 添加过滤器链：XSS过滤器 -> JWT过滤器 -> 租户过滤器
            .addFilterBefore(xssFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(jwtAuthorizationFilter, XssFilter.class)
            .addFilterAfter(tenantFilter, JwtAuthorizationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}