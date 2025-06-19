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

import org.springframework.security.core.AuthenticationException; // 导入此异常
import org.springframework.security.authentication.BadCredentialsException; // 导入此异常
import io.jsonwebtoken.ExpiredJwtException; // 导入JWT库的特定异常
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;

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
            // 捕获更具体的 JWT 异常，并直接抛出 Spring Security 的认证异常
            // 这些异常会被 Spring Security 的 ExceptionTranslationFilter 捕获并转交给 AuthenticationEntryPoint 处理
            catch (ExpiredJwtException e) {
                log.warn("JWT Token expired: {}", e.getMessage());
                // Token 过期，抛出认证失败异常
                throw new BadCredentialsException("JWT Token is expired.", e);
            }
            catch (MalformedJwtException | SignatureException e) {
                log.warn("Invalid JWT Token: {}", e.getMessage());
                // Token 格式错误或签名无效，抛出认证失败异常
                throw new BadCredentialsException("Invalid JWT Token.", e);
            }
            catch (Exception e) // 捕获其他任何潜在异常
            {
                log.warn("Unable to get JWT Token or other JWT error: {}", e.getMessage());
                // 其他 JWT 处理异常，抛出认证失败异常
                throw new BadCredentialsException("Could not process JWT Token.", e);
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
                // JWT Token 验证失败（例如用户名不匹配），抛出认证失败异常
                throw new BadCredentialsException("JWT Token validation failed.");
            }
        }
        // 继续过滤器链。如果在此之前抛出了认证异常，此行代码将不会执行。
        // 如果没有 JWT 或者 JWT 成功认证，则继续正常处理。
        filterChain.doFilter(request, response);
    }
}
