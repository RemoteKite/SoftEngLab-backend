package com.harukite.canteen.controller;

import com.harukite.canteen.dto.BanquetReservationRequest;
import com.harukite.canteen.dto.BanquetReservationResponse;
import com.harukite.canteen.exception.ResourceNotFoundException;
import com.harukite.canteen.model.BanquetStatus;
import com.harukite.canteen.model.User;
import com.harukite.canteen.service.BanquetReservationService;
import com.harukite.canteen.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * REST 控制器，用于管理宴会预订。
 * 提供宴会预订的创建、查询、更新和取消的 API 接口。
 */
@RestController
@RequestMapping("/api/banquet")
@RequiredArgsConstructor
public class BanquetReservationController
{

    private final BanquetReservationService banquetReservationService;
    private final UserRepository userRepository;

    /**
     * 创建新的宴会预订。
     * URL: POST /api/banquet
     * (需要已认证用户权限，通常是学生或任何普通用户)
     *
     * @param request 包含预订信息的 DTO
     * @return 创建成功的宴会预订响应 DTO
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()") // 任何已认证用户都可以创建预订
    public ResponseEntity<BanquetReservationResponse> createBanquetReservation(@Valid @RequestBody BanquetReservationRequest request)
    {
        // 从 Spring Security 认证上下文中获取当前用户名
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with Name: " + userName));
        String userId = user.getUserId(); // 获取用户ID

        BanquetReservationResponse createdReservation = banquetReservationService.createBanquetReservation(request, userId);
        return new ResponseEntity<>(createdReservation, HttpStatus.CREATED);
    }

    /**
     * 根据预订ID获取宴会预订详情。
     * URL: GET /api/banquet/{id}
     * (需要已认证用户权限，且用户是预订所有者或管理员/工作人员)
     *
     * @param id 宴会预订ID
     * @return 宴会预订响应 DTO
     */
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or hasRole('STAFF') or @banquetReservationService.getBanquetReservationById(#id).userId == (userRepository.findByUsername(authentication.name)).getUserId())")
    // 只有管理员/工作人员或预订所有者才能查看
    public ResponseEntity<BanquetReservationResponse> getBanquetReservationById(@PathVariable String id)
    {
        BanquetReservationResponse reservation = banquetReservationService.getBanquetReservationById(id);
        return ResponseEntity.ok(reservation);
    }

    /**
     * 获取所有宴会预订列表。
     * URL: GET /api/banquet
     * (需要管理员权限)
     *
     * @return 宴会预订响应 DTO 列表
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')") // 只有管理员才能获取所有预订列表
    public ResponseEntity<List<BanquetReservationResponse>> getAllBanquetReservations()
    {
        List<BanquetReservationResponse> reservations = banquetReservationService.getAllBanquetReservations();
        return ResponseEntity.ok(reservations);
    }

    /**
     * 根据用户ID获取其所有宴会预订。
     * URL: GET /api/banquet/user/{userId}
     * (用户可以查询自己的预订，管理员可以查询任何用户的预订)
     *
     * @param userId 用户ID
     * @return 宴会预订响应 DTO 列表
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("isAuthenticated() and (hasRole('ADMIN') or #userId == userRepository.findByUsername(authentication.name))")
    // 只有管理员或用户本人才能查看
    public ResponseEntity<List<BanquetReservationResponse>> getBanquetReservationsByUserId(@PathVariable String userId)
    {
        List<BanquetReservationResponse> reservations = banquetReservationService.getBanquetReservationsByUserId(userId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * 获取当前用户的所有宴会预订。
     * URL: GET /api/banquet/current-user
     * (用户可以查询自己的预订，管理员可以查询任何用户的预订)
     *
     * @return 宴会预订响应 DTO 列表
     */
    @GetMapping("/current-user")
    @PreAuthorize("isAuthenticated()") // 任何已认证用户都可以查看自己的预订
    public ResponseEntity<List<BanquetReservationResponse>> getBanquetReservationsByCurrentUser()
    {
        // 从 Spring Security 认证上下文中获取当前用户名
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with Name: " + userName));
        String userId = user.getUserId(); // 获取用户ID

        List<BanquetReservationResponse> reservations = banquetReservationService.getBanquetReservationsByUserId(userId);
        return ResponseEntity.ok(reservations);
    }


    /**
     * 根据食堂ID获取宴会预订列表。
     * URL: GET /api/banquet/canteen/{canteenId}
     * (任何已认证用户或匿名用户都可以查看，通常用于查询食堂的预订情况)
     *
     * @param canteenId 食堂ID
     * @return 宴会预订响应 DTO 列表
     */
    @GetMapping("/canteen/{canteenId}")
    @PreAuthorize("permitAll()") // 允许所有用户访问
    public ResponseEntity<List<BanquetReservationResponse>> getBanquetReservationsByCanteenId(@PathVariable String canteenId)
    {
        List<BanquetReservationResponse> reservations = banquetReservationService.getBanquetReservationsByCanteenId(canteenId);
        return ResponseEntity.ok(reservations);
    }

    /**
     * 更新宴会预订状态。
     * URL: PUT /api/banquet/{id}/status
     * (通常需要管理员或食堂工作人员权限)
     *
     * @param id        宴会预订ID
     * @param newStatus 新的预订状态
     * @return 更新后的宴会预订响应 DTO
     */
    @PutMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')") // 只有管理员或工作人员才能更新状态
    public ResponseEntity<BanquetReservationResponse> updateBanquetStatus(
            @PathVariable String id,
            @RequestParam BanquetStatus newStatus)
    { // 使用 @RequestParam 接收枚举
        BanquetReservationResponse updatedReservation = banquetReservationService.updateBanquetStatus(id, newStatus);
        return ResponseEntity.ok(updatedReservation);
    }

    /**
     * 取消宴会预订。
     * URL: PUT /api/banquet/{id}/cancel
     * (用户可以取消自己的预订，管理员可以取消任何预订)
     *
     * @param id 宴会预订ID
     * @return 无内容响应
     */
    @PutMapping("/{id}/cancel")
    // 只有管理员/工作人员或预订所有者才能取消
    public ResponseEntity<Void> cancelBanquetReservation(@PathVariable String id)
    {
        // 从 Spring Security 认证上下文中获取当前用户名
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userName = authentication.getName();
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with Name: " + userName));
        String userId = user.getUserId(); // 获取用户ID

        banquetReservationService.cancelBanquetReservation(id, userId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 检查某个包厢在指定日期和时间段是否可用。
     * URL: GET /api/banquet/check
     * (任何已认证用户或匿名用户都可以查看)
     *
     * @param roomId 包厢ID
     * @param date   预订日期 (格式:YYYY-MM-DD)
     * @param time   预订时间 (格式: HH:MM)
     * @return 如果包厢可用则为 true，否则为 false
     */
    @GetMapping("/check")
    @PreAuthorize("permitAll()") // 允许所有用户访问
    public ResponseEntity<Boolean> checkRoomAvailability(
            @RequestParam String roomId,
            @RequestParam LocalDate date,
            @RequestParam LocalTime time)
    {
        // 在这里，我们不需要排除任何现有的 banquetId，因为是检查新的可用性
        boolean isAvailable = banquetReservationService.isRoomAvailable(roomId, date, time, null);
        return ResponseEntity.ok(isAvailable);
    }
}