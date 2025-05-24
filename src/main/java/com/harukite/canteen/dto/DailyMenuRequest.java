package com.harukite.canteen.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO for daily menu creation/update requests.
 * Used by administrators to publish daily menus.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DailyMenuRequest
{

    @NotBlank(message = "Canteen ID cannot be empty")
    private String canteenId;

    @NotNull(message = "Menu date cannot be null")
    @FutureOrPresent(message = "Menu date must be today or in the future")
    private LocalDate menuDate;

    @NotNull(message = "Start time cannot be null")
    private LocalTime startTime;

    @NotNull(message = "End time cannot be null")
    private LocalTime endTime;

    @NotEmpty(message = "Menu must contain at least one dish")
    private List<String> dishIds; // List of dish IDs included in this menu

    // publishedByUserId will be extracted from authentication context, not from request body
}