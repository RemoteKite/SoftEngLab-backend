package com.harukite.canteen.controller;

import com.harukite.canteen.dto.AuthResponse;
import com.harukite.canteen.dto.LoginRequest;
import com.harukite.canteen.dto.UserRegistrationRequest;
import com.harukite.canteen.dto.UserResponseDto;
import com.harukite.canteen.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST 控制器，用于用户认证和注册。
 * 提供用户登录和注册的 API 接口。
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController
{

    private final UserService userService;

    /**
     * 用户注册。
     * URL: POST /api/auth/register
     *
     * @param request 用户注册请求 DTO
     * @return 注册成功的用户响应 DTO
     */
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@Valid @RequestBody UserRegistrationRequest request)
    {
        UserResponseDto registeredUser = userService.registerUser(request);
        return new ResponseEntity<>(registeredUser, HttpStatus.CREATED);
    }

    /**
     * 用户登录。
     * URL: POST /api/auth/login
     *
     * @param request 登录请求 DTO
     * @return 认证响应 DTO (包含 JWT token 和用户基本信息)
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginUser(@Valid @RequestBody LoginRequest request)
    {
        AuthResponse authResponse = userService.loginUser(request);
        return ResponseEntity.ok(authResponse);
    }
}
