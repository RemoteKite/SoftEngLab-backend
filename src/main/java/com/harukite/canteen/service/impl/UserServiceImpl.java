package com.harukite.canteen.service.impl;

import com.harukite.canteen.dto.AuthResponse;
import com.harukite.canteen.dto.LoginRequest;
import com.harukite.canteen.dto.UserRegistrationRequest;
import com.harukite.canteen.dto.UserResponseDto;
import com.harukite.canteen.exception.DuplicateEntryException;
import com.harukite.canteen.exception.InvalidInputException;
import com.harukite.canteen.exception.ResourceNotFoundException;
import com.harukite.canteen.model.Allergen;
import com.harukite.canteen.model.DietaryTag;
import com.harukite.canteen.model.User;
import com.harukite.canteen.model.UserRole;
import com.harukite.canteen.repository.AllergenRepository;
import com.harukite.canteen.repository.DietaryTagRepository;
import com.harukite.canteen.repository.UserRepository;
import com.harukite.canteen.service.CustomUserDetailsService;
import com.harukite.canteen.service.UserService;
import com.harukite.canteen.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户服务接口的实现类。
 * 包含用户注册、登录、信息管理等业务逻辑。
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService
{

    private final UserRepository userRepository;
    private final DietaryTagRepository dietaryTagRepository;
    private final AllergenRepository allergenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager; // 注入 AuthenticationManager
    private final JwtUtil jwtUtil; // 注入 JwtUtil
    private final CustomUserDetailsService userDetailsService; // 注入 CustomUserDetailsService

    /**
     * 注册新用户。
     *
     * @param request 用户注册请求 DTO
     * @return 注册成功的用户响应 DTO
     * @throws DuplicateEntryException 如果用户名、邮箱或电话号码已存在
     */
    @Override
    @Transactional
    public UserResponseDto registerUser(UserRegistrationRequest request)
    {
        // 检查用户名是否已存在
        if (userRepository.findByUsername(request.getUsername()).isPresent())
        {
            throw new DuplicateEntryException("Username '" + request.getUsername() + "' already exists.");
        }
        // 检查邮箱是否已存在
        if (request.getEmail() != null && userRepository.findByEmail(request.getEmail()).isPresent())
        {
            throw new DuplicateEntryException("Email '" + request.getEmail() + "' already exists.");
        }
        // 检查电话号码是否已存在
        if (request.getPhoneNumber() != null && userRepository.findByPhoneNumber(request.getPhoneNumber()).isPresent())
        {
            throw new DuplicateEntryException("Phone number '" + request.getPhoneNumber() + "' already exists.");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRole(request.getRole());

        // 处理用户饮食偏好
        if (request.getSelectedDietaryTagNames() != null && !request.getSelectedDietaryTagNames().isEmpty())
        {
            Set<DietaryTag> tags = request.getSelectedDietaryTagNames().stream()
                    .map(tagName -> dietaryTagRepository.findByTagName(tagName)
                            .orElseThrow(() -> new ResourceNotFoundException("Dietary tag not found: " + tagName)))
                    .collect(Collectors.toSet());
            user.setDietaryTags(tags);
        }
        else
        {
            user.setDietaryTags(new HashSet<>()); // 如果没有选择，则设置为空集合
        }

        // 处理用户过敏原
        if (request.getSelectedAllergenNames() != null && !request.getSelectedAllergenNames().isEmpty())
        {
            Set<Allergen> allergens = request.getSelectedAllergenNames().stream()
                    .map(allergenName -> allergenRepository.findByAllergenName(allergenName)
                            .orElseThrow(() -> new ResourceNotFoundException("Allergen not found: " + allergenName)))
                    .collect(Collectors.toSet());
            user.setAllergens(allergens);
        }
        else
        {
            user.setAllergens(new HashSet<>()); // 如果没有选择，则设置为空集合
        }

        User savedUser = userRepository.save(user);
        return convertToDto(savedUser);
    }

    /**
     * 用户登录。
     *
     * @param request 登录请求 DTO
     * @return 认证响应 DTO (包含 JWT token 和用户基本信息)
     * @throws ResourceNotFoundException 如果用户不存在
     * @throws InvalidInputException     如果密码不匹配
     */
    @Override
    @Transactional(readOnly = true)
    public AuthResponse loginUser(LoginRequest request)
    {
        // 使用 AuthenticationManager 进行认证
        // 如果认证失败，authenticationManager.authenticate() 会抛出 AuthenticationException
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword())
        );
        // 如果认证成功，将认证信息设置到 SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 从认证对象中获取 UserDetails
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsernameOrEmail());

        // 生成 JWT Token
        String jwtToken = jwtUtil.generateToken(userDetails);

        // 获取实际的用户实体以获取 userId 和 role
        User user = userRepository.findByUsername(request.getUsernameOrEmail())
                .orElseGet(() -> userRepository.findByEmail(request.getUsernameOrEmail())
                        .orElseThrow(() -> new ResourceNotFoundException("User not found after authentication: " + request.getUsernameOrEmail())));


        return new AuthResponse(user.getUserId(), user.getUsername(), user.getRole().getValue(), jwtToken, "Login successful");
    }

    /**
     * 根据用户ID获取用户详情。
     *
     * @param userId 用户ID
     * @return 用户响应 DTO
     * @throws ResourceNotFoundException 如果用户不存在
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(String userId)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        return convertToDto(user);
    }

    /**
     * 获取所有用户列表。
     *
     * @return 用户响应 DTO 列表
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers()
    {
        return userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 更新用户信息。
     *
     * @param userId         要更新的用户ID
     * @param updatedUserDto 包含更新信息的用户响应 DTO
     * @return 更新后的用户响应 DTO
     * @throws ResourceNotFoundException 如果用户不存在
     * @throws DuplicateEntryException   如果更新后的邮箱或电话号码已存在
     */
    @Override
    @Transactional
    public UserResponseDto updateUser(String userId, UserResponseDto updatedUserDto)
    {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        if (updatedUserDto.getEmail() != null && !updatedUserDto.getEmail().equals(existingUser.getEmail()))
        {
            userRepository.findByEmail(updatedUserDto.getEmail()).ifPresent(user -> {
                if (!user.getUserId().equals(userId))
                {
                    throw new DuplicateEntryException("Email '" + updatedUserDto.getEmail() + "' already exists for another user.");
                }
            });
        }
        if (updatedUserDto.getPhoneNumber() != null && !updatedUserDto.getPhoneNumber().equals(existingUser.getPhoneNumber()))
        {
            userRepository.findByPhoneNumber(updatedUserDto.getPhoneNumber()).ifPresent(user -> {
                if (!user.getUserId().equals(userId))
                {
                    throw new DuplicateEntryException("Phone number '" + updatedUserDto.getPhoneNumber() + "' already exists for another user.");
                }
            });
        }

        existingUser.setEmail(updatedUserDto.getEmail());
        existingUser.setPhoneNumber(updatedUserDto.getPhoneNumber());
        existingUser.setRole(updatedUserDto.getRole());

        // 更新饮食偏好
        if (updatedUserDto.getDietaryTagNames() != null)
        {
            Set<DietaryTag> newTags = updatedUserDto.getDietaryTagNames().stream()
                    .map(tagName -> dietaryTagRepository.findByTagName(tagName)
                            .orElseThrow(() -> new ResourceNotFoundException("Dietary tag not found: " + tagName)))
                    .collect(Collectors.toSet());
            existingUser.setDietaryTags(newTags);
        }
        else
        {
            existingUser.setDietaryTags(new HashSet<>());
        }

        // 更新用户过敏原
        if (updatedUserDto.getAllergenNames() != null)
        {
            Set<Allergen> newAllergens = updatedUserDto.getAllergenNames().stream()
                    .map(allergenName -> allergenRepository.findByAllergenName(allergenName)
                            .orElseThrow(() -> new ResourceNotFoundException("Allergen not found: " + allergenName)))
                    .collect(Collectors.toSet());
            existingUser.setAllergens(newAllergens);
        }
        else
        {
            existingUser.setAllergens(new HashSet<>());
        }

        User savedUser = userRepository.save(existingUser);
        return convertToDto(savedUser);
    }

    /**
     * 更新用户角色。
     *
     * @param userId  要更新的用户ID
     * @param newRole 新的用户角色
     * @return 更新后的用户响应 DTO
     * @throws ResourceNotFoundException 如果用户不存在
     */
    @Transactional
    public UserResponseDto updateUserRole(String userId, UserRole newRole)
    {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        existingUser.setRole(newRole);
        User savedUser = userRepository.save(existingUser);
        return convertToDto(savedUser);
    }

    /**
     * 删除用户。
     *
     * @param userId 要删除的用户ID
     * @throws ResourceNotFoundException 如果用户不存在
     */
    @Override
    @Transactional
    public void deleteUser(String userId)
    {
        if (!userRepository.existsById(userId))
        {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
        userRepository.deleteById(userId);
    }

    /**
     * 辅助方法：将 User 实体转换为 UserResponseDto。
     *
     * @param user User 实体
     * @return UserResponseDto
     */
    private UserResponseDto convertToDto(User user)
    {
        Set<String> dietaryTagNames = user.getDietaryTags().stream()
                .map(DietaryTag::getTagName)
                .collect(Collectors.toSet());

        Set<String> allergenNames = user.getAllergens().stream()
                .map(Allergen::getAllergenName)
                .collect(Collectors.toSet());

        return new UserResponseDto(
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getRole(),
                user.getCreatedAt(),
                dietaryTagNames,
                allergenNames
        );
    }
}