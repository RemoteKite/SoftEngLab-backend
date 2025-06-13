package com.harukite.canteen.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Banquet Reservation Dish Item information.
 * Used for specifying individual dishes and their quantities within a banquet reservation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BanquetReservationDishItemDto {
    private String banquetReservationDishItemId; // 对于现有项，可能为 null
    private String banquetId; // 所属宴会预订ID (只在响应中可能需要)

    @NotBlank(message = "Dish ID cannot be empty")
    private String dishId;

    private String dishName; // 菜品名称（响应时显示）
    private BigDecimal dishPrice; // 菜品价格（响应时显示，或用于计算小计）

    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    private BigDecimal subtotal; // 该菜品项的小计金额
}
