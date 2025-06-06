package com.harukite.canteen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * DTO for Dish information.
 * Used for displaying dish details, including associated tags and allergens.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DishDto
{
    private String canteenId; // Only ID, full Canteen object might not be needed
    private String name;
    private String description;
    private BigDecimal price;
    private String imageUrl;
    private List<String> dietaryTagIds; // List of dietary tag IDs
    private List<String> allergenIds; // List of allergen IDs
    //TODO Below fields are used in response,consider splitting into separate DTOs if needed
    private String dishId;
    private Boolean isAvailable; //暂未使用，考虑弃用
    private LocalDateTime createdAt;
    private List<String> dietaryTagNames; // List of dietary tag names
    private List<String> allergenNames; // List of allergen names
    private Double averageRating;
}
