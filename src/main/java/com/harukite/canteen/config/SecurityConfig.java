package com.harukite.canteen.config;

import com.harukite.canteen.filter.JwtRequestFilter;
import com.harukite.canteen.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 安全配置类，用于定义 Spring Security 相关的 Bean。
 */
@Configuration // 标记这是一个配置类，Spring 会扫描并处理它
@EnableWebSecurity // 启用 Spring Security 的 Web 安全功能
@EnableMethodSecurity(prePostEnabled = true) // 启用方法级别的安全，prePostEnabled = true 允许使用 @PreAuthorize 和 @PostAuthorize
@RequiredArgsConstructor // Lombok 注解，用于自动生成包含所有 final 字段的构造函数
public class SecurityConfig
{

    private final CustomUserDetailsService userDetailsService;
    private final JwtRequestFilter jwtRequestFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint; // 注入自定义的认证入口点

    /**
     * 定义一个 PasswordEncoder 的 Bean。
     * 使用 BCryptPasswordEncoder 是业界推荐的安全密码编码器。
     *
     * @return PasswordEncoder 实例
     */
    @Bean // 标记这个方法返回一个由 Spring 管理的 Bean
    public PasswordEncoder passwordEncoder()
    {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置 AuthenticationManager。
     * AuthenticationManager 负责处理认证请求。
     *
     * @param authenticationConfiguration 认证配置
     * @return AuthenticationManager 实例
     * @throws Exception 获取 AuthenticationManager 时可能抛出的异常
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception
    {
        return authenticationConfiguration.getAuthenticationManager();
    }

    /**
     * 配置 SecurityFilterChain，定义 HTTP 请求级别的安全规则。
     *
     * @param http HttpSecurity 对象
     * @return 配置好的 SecurityFilterChain
     * @throws Exception 配置过程中可能抛出的异常
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception
    {
        // 创建 DaoAuthenticationProvider 实例
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService); // 设置自定义的 UserDetailsService
        authProvider.setPasswordEncoder(passwordEncoder()); // 设置密码编码器

        http
                .csrf(csrf -> csrf.disable()) // 禁用 CSRF，因为我们使用 JWT 进行无状态认证
                .cors(Customizer.withDefaults()) // 启用 CORS 配置
                .authorizeHttpRequests(authorize -> authorize
                        // 允许匿名访问的公共接口，例如注册和登录
                        .requestMatchers("/api/auth/**").permitAll()
                        // 允许所有用户访问 /api/canteens 下的所有路径
                        .requestMatchers("/api/canteens/**").permitAll()
                        .requestMatchers("/api/get-advice").permitAll()
                        // 允许 Swagger UI 和 API 文档访问 (如果需要)
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/webjars/**"
                        ).permitAll()
                        // 管理员接口需要 'ADMIN' 角色
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated()
                )
                // 配置异常处理，特别是认证入口点
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)) // 关键：将自定义的 JwtAuthenticationEntryPoint 注册到 Spring Security
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 设置为无状态会话
                )
                .authenticationProvider(authProvider) // 使用认证提供者
                // 在 UsernamePasswordAuthenticationFilter 之前添加 JWT 过滤器
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
