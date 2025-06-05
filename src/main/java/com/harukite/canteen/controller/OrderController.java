package com.harukite.canteen.controller;

import com.harukite.canteen.dto.OrderRequest;
import com.harukite.canteen.dto.OrderResponse;
import com.harukite.canteen.exception.ResourceNotFoundException;
import com.harukite.canteen.model.OrderStatus;
import com.harukite.canteen.model.User;
import com.harukite.canteen.repository.UserRepository;
import com.harukite.canteen.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST 控制器，用于管理餐品预订（订单）。
 * 提供订单的创建、查询、更新状态和取消的 API 接口。
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController
{

    private final OrderService orderService;
    private final UserRepository userRepository;

    /**
     * 创建新订单。
     * URL: POST /api/orders
     *
     * @param request 包含订单信息的 DTO
     * @return 创建成功的订单响应 DTO
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderRequest request)
    {
        // 从 Spring Security 认证上下文中获取当前用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user= userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with Name: " + authentication.getName()));
        String userId = user.getUserId(); // 获取用户ID
        OrderResponse createdOrder = orderService.createOrder(request, userId);
        return new ResponseEntity<>(createdOrder, HttpStatus.CREATED);
    }

    /**
     * 根据订单ID获取订单详情。
     * URL: GET /api/orders/{id}
     *
     * @param id 订单ID
     * @return 订单响应 DTO
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable String id)
    {
        OrderResponse order = orderService.getOrderById(id);
        return ResponseEntity.ok(order);
    }

    /**
     * 获取所有订单列表。
     * URL: GET /api/orders
     * (通常需要管理员权限)
     *
     * @return 订单响应 DTO 列表
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders()
    {
        List<OrderResponse> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    /**
     * 根据用户ID获取其所有订单。
     * URL: GET /api/orders/user/{userId}
     * (用户可以查询自己的订单，管理员可以查询任何用户的订单)
     *
     * @param userId 用户ID
     * @return 订单响应 DTO 列表
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByUserId(@PathVariable String userId)
    {
        List<OrderResponse> orders = orderService.getOrdersByUserId(userId);
        return ResponseEntity.ok(orders);
    }

    /**
     * 更新订单状态。
     * URL: PUT /api/orders/{id}/status
     * (通常需要管理员或食堂工作人员权限)
     *
     * @param id        订单ID
     * @param newStatus 新的订单状态
     * @return 更新后的订单响应 DTO
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable String id,
            @RequestParam OrderStatus newStatus)
    { // 使用 @RequestParam 接收枚举
        OrderResponse updatedOrder = orderService.updateOrderStatus(id, newStatus);
        return ResponseEntity.ok(updatedOrder);
    }

    /**
     * 取消订单。
     * URL: PUT /api/orders/{id}/cancel
     * (用户可以取消自己的订单，管理员可以取消任何订单)
     *
     * @param id 订单ID
     * @return 无内容响应
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable String id)
    {
        // 从 Spring Security 认证上下文中获取当前用户ID
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user= userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with Name: " + authentication.getName()));
        orderService.cancelOrder(id, user.getUserId());
        return ResponseEntity.noContent().build();
    }
}
