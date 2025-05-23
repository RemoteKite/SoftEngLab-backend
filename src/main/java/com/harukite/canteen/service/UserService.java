package com.harukite.canteen.service;

import com.harukite.canteen.dto.AuthResponse;
import com.harukite.canteen.dto.LoginRequest;
import com.harukite.canteen.dto.UserRegistrationRequest;
import com.harukite.canteen.dto.UserResponseDto;
import com.harukite.canteen.model.UserRole;

import java.util.List;

/**
 * 用户服务接口。
 * 定义用户相关的业务操作。
 */
public interface UserService
{

    /**
     * 注册新用户。
     *
     * @param request 用户注册请求 DTO
     * @return 注册成功的用户响应 DTO
     */
    UserResponseDto registerUser(UserRegistrationRequest request);

    /**
     * 用户登录。
     *
     * @param request 登录请求 DTO (包含用户名/邮箱和密码)
     * @return 认证响应 DTO (包含 JWT token 和用户基本信息)
     */
    AuthResponse loginUser(LoginRequest request);

    /**
     * 根据用户ID获取用户详情。
     *
     * @param userId 用户ID
     * @return 用户响应 DTO
     */
    UserResponseDto getUserById(String userId);

    /**
     * 获取所有用户列表。
     *
     * @return 用户响应 DTO 列表
     */
    List<UserResponseDto> getAllUsers();

    /**
     * 更新用户信息。
     *
     * @param userId         要更新的用户ID
     * @param updatedUserDto 包含更新信息的用户响应 DTO
     * @return 更新后的用户响应 DTO
     */
    UserResponseDto updateUser(String userId, UserResponseDto updatedUserDto);

    /**
     * 删除用户。
     *
     * @param userId 要删除的用户ID
     */
    void deleteUser(String userId);

    /**
     * 更新用户角色。
     *
     * @param userId  用户ID
     * @param newRole 新角色
     * @return 更新后的用户响应 DTO
     */
    UserResponseDto updateUserRole(String userId, UserRole newRole);
}
