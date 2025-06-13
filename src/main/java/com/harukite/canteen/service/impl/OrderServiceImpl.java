package com.harukite.canteen.service.impl;

import com.harukite.canteen.dto.OrderRequest;
import com.harukite.canteen.dto.OrderResponse;
import com.harukite.canteen.exception.InvalidInputException;
import com.harukite.canteen.exception.ResourceNotFoundException;
import com.harukite.canteen.model.*;
import com.harukite.canteen.repository.*;
import com.harukite.canteen.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 订单服务接口的实现类。
 * 包含订单的创建、查询、状态更新和取消等业务逻辑。
 */
@Service("orderService")
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService
{

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final CanteenRepository canteenRepository;
    private final DishRepository dishRepository;

    /**
     * 创建新订单。
     *
     * @param request 包含订单信息的 DTO
     * @param userId 下单用户
     * @return 创建成功的订单响应 DTO
     * @throws ResourceNotFoundException 如果用户、食堂或菜品不存在
     * @throws InvalidInputException     如果订单项为空或菜品数量无效
     */
    @Override
    @Transactional
    public OrderResponse createOrder(OrderRequest request, String userId)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        Canteen canteen = canteenRepository.findById(request.getCanteenId())
                .orElseThrow(() -> new ResourceNotFoundException("Canteen not found with ID: " + request.getCanteenId()));

        if (request.getItems() == null || request.getItems().isEmpty())
        {
            throw new InvalidInputException("Order must contain at least one item.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setCanteen(canteen);
        order.setOrderDate(request.getOrderDate());
        order.setPickupTime(request.getPickupTime());
        order.setStatus(OrderStatus.PENDING); // 新订单默认为待处理状态

        BigDecimal totalAmount = BigDecimal.ZERO;
        Set<OrderItem> orderItems = new HashSet<>();

        for (OrderRequest.OrderItemRequest itemDto : request.getItems())
        {
            Dish dish = dishRepository.findById(itemDto.getDishId())
                    .orElseThrow(() -> new ResourceNotFoundException("Dish not found with ID: " + itemDto.getDishId()));

            if (itemDto.getQuantity() <= 0)
            {
                throw new InvalidInputException("Dish quantity must be greater than zero for dish: " + dish.getName());
            }

            BigDecimal subtotal = dish.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setDish(dish);
            orderItem.setQuantity(itemDto.getQuantity());
            orderItem.setSubtotal(subtotal);
            orderItem.setOrder(order); // 设置双向关联
            orderItems.add(orderItem);
        }

        order.setTotalAmount(totalAmount);
        order.setOrderItems(orderItems); // 设置订单项集合

        Order savedOrder = orderRepository.save(order);
        orderRepository.flush(); // 确保订单和订单项都已保存到数据库

        return convertToDto(savedOrder);
    }

    /**
     * 根据订单ID获取订单详情。
     *
     * @param orderId 订单ID
     * @return 订单响应 DTO
     * @throws ResourceNotFoundException 如果订单不存在
     */
    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(String orderId)
    {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        return convertToDto(order);
    }

    /**
     * 获取所有订单列表。
     *
     * @return 订单响应 DTO 列表
     */
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders()
    {
        return orderRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 根据用户ID获取其所有订单。
     *
     * @param userId 用户ID
     * @return 订单响应 DTO 列表
     * @throws ResourceNotFoundException 如果用户不存在
     */
    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(String userId)
    {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        List<Order> orders = orderRepository.findByUser(user);
        return orders.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 更新订单状态。
     *
     * @param orderId   订单ID
     * @param newStatus 新的订单状态
     * @return 更新后的订单响应 DTO
     * @throws ResourceNotFoundException 如果订单不存在
     * @throws InvalidInputException     如果状态转换无效 (例如，尝试从 COMPLETED 转换为 PENDING)
     */
    @Override
    @Transactional
    public OrderResponse updateOrderStatus(String orderId, OrderStatus newStatus)
    {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        // 简单的状态转换逻辑示例，可以根据业务需求进行扩展
        if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.COMPLETED)
        {
            if (newStatus != OrderStatus.CANCELLED && newStatus != OrderStatus.COMPLETED)
            { // 已取消或已完成的订单不能再次转换为其他状态 (除了再次设为已完成或取消，但意义不大)
                throw new InvalidInputException("Cannot change status from " + order.getStatus() + " to " + newStatus);
            }
        }
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        return convertToDto(updatedOrder);
    }

    /**
     * 取消订单。
     *
     * @param orderId 订单ID
     * @param userId  操作用户ID (用于权限检查)
     * @throws ResourceNotFoundException 如果订单不存在
     * @throws InvalidInputException     如果订单状态不允许取消或用户没有权限
     */
    @Override
    @Transactional
    public void cancelOrder(String orderId, String userId)
    {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        // 权限检查：确保只有订单所有者或具有管理员权限的用户才能取消
        if (!order.getUser().getUserId().equals(userId) /* && !userRepository.findById(userId).get().getRole().equals(UserRole.ADMIN) */)
        {
            // 假设这里有获取用户角色并进行判断的逻辑
            // 暂时简化处理，只允许订单所有者取消
            throw new InvalidInputException("You are not authorized to cancel this order.");
        }

        // 检查订单状态是否允许取消
        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED)
        {
            throw new InvalidInputException("Order cannot be cancelled as its current status is " + order.getStatus());
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    /**
     * 辅助方法：将 Order 实体转换为 OrderResponse DTO。
     *
     * @param order Order 实体
     * @return OrderResponse DTO
     */
    private OrderResponse convertToDto(Order order)
    {
        List<OrderResponse.OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(item -> new OrderResponse.OrderItemResponse(
                        item.getOrderItemId(),
                        item.getDish().getDishId(),
                        item.getDish().getName(),
                        item.getDish().getPrice(), // 菜品在下单时的价格
                        item.getQuantity(),
                        item.getSubtotal()
                ))
                .collect(Collectors.toList());

        return new OrderResponse(
                order.getOrderId(),
                order.getUser().getUserId(),
                order.getUser().getUsername(),
                order.getCanteen().getCanteenId(),
                order.getCanteen().getName(),
                order.getOrderDate(),
                order.getPickupTime(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getCreatedAt(),
                itemResponses
        );
    }
}