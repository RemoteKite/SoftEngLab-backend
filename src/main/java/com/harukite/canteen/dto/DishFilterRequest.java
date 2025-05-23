package com.harukite.canteen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO for filtering dish requests.
 * Used to receive filter criteria from the client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DishFilterRequest
{
    private String canteenId;
    private String dishName;
    private BigDecimal minPrice;
    private BigDecimal maxPrice;
    private Boolean isAvailable;
    private List<String> requiredDietaryTags; // e.g., ["素食", "清真"]
    private List<String> excludedAllergens; // e.g., ["花生", "牛奶"]
}
