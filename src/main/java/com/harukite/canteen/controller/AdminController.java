package com.harukite.canteen.controller;

import com.harukite.canteen.dto.UserResponseDto;
import com.harukite.canteen.model.UserRole;
import com.harukite.canteen.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST 控制器，用于管理员专属功能。
 * 主要提供用户管理等系统级 API 接口。
 * 这些接口严格限制为需要 'ADMIN' 角色。
 */
@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController
{

    private final UserService userService;

    // --- 用户管理 ---

    /**
     * 获取所有用户列表。
     * URL: GET /api/admin/users
     * (需要管理员权限)
     *
     * @return 用户响应 DTO 列表
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')") // 只有拥有 'ADMIN' 角色的用户才能访问
    public ResponseEntity<List<UserResponseDto>> getAllUsers()
    {
        List<UserResponseDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * 更新用户角色。
     * URL: PUT /api/admin/users/{userId}/role
     * (需要管理员权限)
     *
     * @param userId  用户ID
     * @param newRole 新的用户角色
     * @return 更新后的用户响应 DTO
     */
    @PutMapping("/users/{userId}/role")
    @PreAuthorize("hasRole('ADMIN')") // 只有拥有 'ADMIN' 角色的用户才能访问
    public ResponseEntity<UserResponseDto> updateUserRole(
            @PathVariable String userId,
            @RequestParam UserRole newRole)
    { // 使用 @RequestParam 接收枚举
        UserResponseDto updatedUser = userService.updateUserRole(userId, newRole);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 重置用户密码。
     * URL: PUT /api/admin/users/{userId}/password
     * (需要管理员权限)
     *
     * @param userId 用户ID
     * @return 用户响应 DTO
     */
    @PutMapping("/users/{userId}/password")
    @PreAuthorize("hasRole('ADMIN')") // 只有拥有 'ADMIN' 角色的用户才能访问
    public ResponseEntity<UserResponseDto> resetUserPassword(@PathVariable String userId, @RequestParam (required = false) String newPassword)
    {
        // 如果 newPassword 为 null，则使用默认密码逻辑
        if (newPassword == null || newPassword.isEmpty()) {
            newPassword = "defaultPassword"; // 这里可以替换为实际的默认密码逻辑
        }
        UserResponseDto updatedUser = userService.resetUserPassword(userId, newPassword);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 删除用户。
     * URL: DELETE /api/admin/users/{userId}
     * (需要管理员权限)
     *
     * @param userId 用户ID
     * @return 无内容响应
     */
    @DeleteMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')") // 只有拥有 'ADMIN' 角色的用户才能访问
    public ResponseEntity<Void> deleteUser(@PathVariable String userId)
    {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
