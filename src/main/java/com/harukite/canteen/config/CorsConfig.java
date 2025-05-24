package com.harukite.canteen.config; // 请根据您的项目结构调整包名

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 全局 CORS 配置类，允许前端进行跨域请求。
 */
@Configuration
public class CorsConfig
{

    @Bean
    public WebMvcConfigurer corsConfigurer()
    {
        return new WebMvcConfigurer()
        {
            @Override
            public void addCorsMappings(CorsRegistry registry)
            {
                registry.addMapping("/**") // 允许所有路径进行跨域访问
                        .allowedOrigins("http://localhost:5173") // 允许的源地址，前端地址
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的 HTTP 方法
                        .allowedHeaders("*") // 允许所有请求头，包括 Authorization 等自定义头
                        .allowCredentials(true) // 允许发送认证信息（如 Cookies, HTTP认证，以及 Authorization header）
                        .maxAge(3600); // 预检请求（OPTIONS）的缓存时间，单位秒
            }
        };
    }
}
