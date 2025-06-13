package com.harukite.canteen.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for Package information.
 * Used for creating, updating, and displaying package details,
 * including associated dishes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PackageDto {
    private String packageId;

    @NotBlank(message = "Canteen ID cannot be empty")
    private String canteenId;
    private String canteenName;

    @NotBlank(message = "Package name cannot be empty")
    private String name;

    private String description;

    @NotNull(message = "Price cannot be null")
    private BigDecimal price;

    private List<String> dishIds; // For requests: list of dish IDs included in the package
    private List<DishDto> dishes; // For responses: list of detailed Dish DTOs
}
