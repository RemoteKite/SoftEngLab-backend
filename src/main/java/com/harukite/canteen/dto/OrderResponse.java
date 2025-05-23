package com.harukite.canteen.dto;

import com.harukite.canteen.model.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO for meal order responses.
 * Used for displaying order details to users.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse
{
    private String orderId;
    private String userId;
    private String username; // Include username for display
    private String canteenId;
    private String canteenName; // Include canteen name for display
    private LocalDate orderDate;
    private LocalTime pickupTime;
    private BigDecimal totalAmount;
    private OrderStatus status;
    private LocalDateTime createdAt;
    private List<OrderItemResponse> items; // List of order item DTOs

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemResponse
    {
        private String orderItemId;
        private String dishId;
        private String dishName; // Include dish name for display
        private BigDecimal dishPrice; // Include dish price at time of order
        private Integer quantity;
        private BigDecimal subtotal;
    }
}
