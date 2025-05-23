package com.harukite.canteen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO for daily menu responses.
 * Used for displaying daily menu details to users.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyMenuResponse
{
    private String menuId;
    private String canteenId;
    private String canteenName; // Include canteen name for display
    private LocalDate menuDate;
    private LocalTime startTime;
    private LocalTime endTime;
    private String publishedByUsername; // Include publisher's username
    private LocalDateTime publishedAt;
    private List<DishDto> dishes; // List of Dish DTOs included in this menu
}
