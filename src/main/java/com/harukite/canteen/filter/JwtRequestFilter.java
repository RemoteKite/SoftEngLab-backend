package com.harukite.canteen.filter;

import com.harukite.canteen.service.CustomUserDetailsService;
import com.harukite.canteen.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 请求过滤器。
 * 拦截所有请求，解析并验证 JWT Token，设置 Spring Security 认证上下文。
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter
{

    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException
    {

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // 检查 Authorization 头是否以 "Bearer " 开头
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer "))
        {
            jwt = authorizationHeader.substring(7); // 提取 Token
            try
            {
                username = jwtUtil.extractUsername(jwt); // 提取用户名
            }
            catch (Exception e)
            {
                log.warn("JWT Token is expired or invalid: {}", e.getMessage());
                // 这里可以添加更具体的错误处理，例如设置 HTTP 401 状态码
            }
        }

        // 如果提取到用户名且当前安全上下文没有认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null)
        {

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 验证 Token
            if (jwtUtil.validateToken(jwt, userDetails))
            {
                // 构建认证 Token
                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 设置认证信息到安全上下文
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                log.debug("User {} authenticated successfully.", username);
            }
            else
            {
                log.warn("JWT Token validation failed for user: {}", username);
            }
        }
        filterChain.doFilter(request, response); // 继续过滤器链
    }
}
