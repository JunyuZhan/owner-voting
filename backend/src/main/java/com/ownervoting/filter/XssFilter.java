package com.ownervoting.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

/**
 * XSS防护过滤器
 * 用于清理请求参数和请求体中的XSS攻击向量
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class XssFilter extends OncePerRequestFilter {

    private static final Pattern[] XSS_PATTERNS = {
        // Script标签
        Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
        // src属性
        Pattern.compile("src[\r\n]*=[\r\n]*\\\'(.*?)\\\'", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        Pattern.compile("src[\r\n]*=[\r\n]*\\\"(.*?)\\\"", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // 不安全的eval表达式
        Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // 表达式语句
        Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // JavaScript代码
        Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
        // VBScript代码
        Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
        // 危险的onload属性
        Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
        // 其他危险事件
        Pattern.compile("on\\w+\\s*=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL)
    };
    
    private static final List<String> EXCLUDED_PATHS = Arrays.asList(
        "/api/auth/login",
        "/api/auth/refresh"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // 检查是否为排除的路径
        String path = request.getRequestURI();
        if (EXCLUDED_PATHS.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // 包装请求以便处理XSS
        XssRequestWrapper wrappedRequest = new XssRequestWrapper(request);
        
        // 添加安全头
        response.setHeader("X-XSS-Protection", "1; mode=block");
        response.setHeader("X-Content-Type-Options", "nosniff");
        
        filterChain.doFilter(wrappedRequest, response);
    }
    
    /**
     * XSS请求包装器
     * 用于清理请求参数中的潜在XSS代码
     */
    private static class XssRequestWrapper extends ContentCachingRequestWrapper {
        
        public XssRequestWrapper(HttpServletRequest request) {
            super(request);
        }
        
        @Override
        public String getParameter(String name) {
            String value = super.getParameter(name);
            return value != null ? sanitize(value) : null;
        }
        
        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (values == null) {
                return null;
            }
            
            String[] sanitizedValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                sanitizedValues[i] = sanitize(values[i]);
            }
            return sanitizedValues;
        }
        
        @Override
        public Map<String, String[]> getParameterMap() {
            Map<String, String[]> paramMap = super.getParameterMap();
            Map<String, String[]> sanitizedParamMap = new HashMap<>();
            
            for (Map.Entry<String, String[]> entry : paramMap.entrySet()) {
                String[] values = entry.getValue();
                String[] sanitizedValues = new String[values.length];
                for (int i = 0; i < values.length; i++) {
                    sanitizedValues[i] = sanitize(values[i]);
                }
                sanitizedParamMap.put(entry.getKey(), sanitizedValues);
            }
            
            return Collections.unmodifiableMap(sanitizedParamMap);
        }
        
        @Override
        public String getHeader(String name) {
            String value = super.getHeader(name);
            return value != null ? sanitize(value) : null;
        }
        
        /**
         * 清理输入字符串，移除潜在的XSS内容
         */
        private String sanitize(String value) {
            if (value == null) {
                return null;
            }
            
            // 先用HTML转义处理
            String sanitized = HtmlUtils.htmlEscape(value);
            
            // 然后对特定模式进行替换
            for (Pattern pattern : XSS_PATTERNS) {
                sanitized = pattern.matcher(sanitized).replaceAll("");
            }
            
            return sanitized;
        }
    }
} 