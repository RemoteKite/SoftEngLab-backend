package com.harukite.canteen.config; // 建议放在 config 包下

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 自定义 JWT 认证入口点。
 * 当用户尝试访问受保护的资源但没有提供凭据或提供了无效凭据时，
 * Spring Security 将调用此类的 commence 方法。
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // 当用户未认证尝试访问受保护资源时，发送 401 Unauthorized 响应
        // authException 包含了认证失败的具体原因（例如 BadCredentialsException）
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: " + authException.getMessage());
    }
}
