package com.harukite.canteen.service;

import com.harukite.canteen.dto.OrderRequest;
import com.harukite.canteen.dto.OrderResponse;
import com.harukite.canteen.model.OrderStatus;

import java.util.List;

/**
 * 订单服务接口。
 * 定义餐品预订相关的业务操作，包括创建、查询、更新和取消订单。
 */
public interface OrderService
{

    /**
     * 创建新订单。
     *
     * @param request 包含订单信息的 DTO
     * @param userId  下单用户ID
     * @return 创建成功的订单响应 DTO
     */
    OrderResponse createOrder(OrderRequest request, String userId);

    /**
     * 根据订单ID获取订单详情。
     *
     * @param orderId 订单ID
     * @return 订单响应 DTO
     */
    OrderResponse getOrderById(String orderId);

    /**
     * 获取所有订单列表。
     *
     * @return 订单响应 DTO 列表
     */
    List<OrderResponse> getAllOrders();

    /**
     * 根据用户ID获取其所有订单。
     *
     * @param userId 用户ID
     * @return 订单响应 DTO 列表
     */
    List<OrderResponse> getOrdersByUserId(String userId);

    /**
     * 更新订单状态。
     *
     * @param orderId   订单ID
     * @param newStatus 新的订单状态
     * @return 更新后的订单响应 DTO
     */
    OrderResponse updateOrderStatus(String orderId, OrderStatus newStatus);

    /**
     * 取消订单。
     *
     * @param orderId 订单ID
     * @param userId  操作用户ID (用于权限检查)
     */
    void cancelOrder(String orderId, String userId);
}
