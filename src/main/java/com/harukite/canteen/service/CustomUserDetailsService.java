package com.harukite.canteen.service;

import com.harukite.canteen.model.User;
import com.harukite.canteen.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

/**
 * 自定义 UserDetailsService 实现。
 * 用于 Spring Security 从数据库加载用户详情。
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService
{

    private final UserRepository userRepository;

    /**
     * 根据用户名（或邮箱）加载用户详情。
     *
     * @param usernameOrEmail 用户名或邮箱
     * @return UserDetails 对象
     * @throws UsernameNotFoundException 如果用户未找到
     */
    @Override
    @Transactional(readOnly = true) // 确保在事务中加载用户及其关联的角色
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException
    {
        // 尝试通过用户名查找，如果未找到则通过邮箱查找
        User user = userRepository.findByUsername(usernameOrEmail)
                .orElseGet(() -> userRepository.findByEmail(usernameOrEmail)
                        .orElseThrow(() -> new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail)));

        // 将 UserRole 转换为 Spring Security 的 GrantedAuthority
        // Spring Security 默认会在 hasRole() 检查时自动添加 "ROLE_" 前缀，
        // 所以这里我们手动添加，以匹配 hasRole() 的期望。
        List<GrantedAuthority> authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        // 返回 Spring Security 的 UserDetails 对象
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), // 作为 principal 的用户名
                user.getPasswordHash(), // 用户的密码哈希
                authorities // 用户的权限/角色
        );
    }
}
