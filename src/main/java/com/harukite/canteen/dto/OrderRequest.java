package com.harukite.canteen.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO for meal order requests.
 * Used by users to place meal orders.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest
{

    @NotBlank(message = "Canteen ID cannot be empty")
    private String canteenId;

    @NotNull(message = "Order date cannot be null")
    @FutureOrPresent(message = "Order date must be today or in the future")
    private LocalDate orderDate;

    @NotNull(message = "Pickup time cannot be null")
    private LocalTime pickupTime;

    @NotEmpty(message = "Order must contain at least one item")
    @Size(min = 1, message = "Order must contain at least one item")
    private List<OrderItemRequest> items; // List of order items

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItemRequest
    {
        @NotBlank(message = "Dish ID cannot be empty")
        private String dishId;

        @NotNull(message = "Quantity cannot be null")
        private Integer quantity;
    }
}
