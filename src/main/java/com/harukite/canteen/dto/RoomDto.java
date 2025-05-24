package com.harukite.canteen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO for Room information.
 * Used for displaying room details to the client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDto
{
    private String roomId;
    private String canteenId;
    private String canteenName; // Include canteen name for display
    private String name;
    private Integer capacity;
    private String description;
    private String imageUrl;
    private BigDecimal baseFee;
}
