package com.harukite.canteen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Canteen information.
 * Used for displaying canteen details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CanteenDto
{
    private String canteenId;
    private String name;
    private String description;
    private String location;
    private String openingHours;
    private String contactPhone;
    private String imageUrl;
}

