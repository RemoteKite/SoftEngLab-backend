package com.harukite.canteen.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO for banquet reservation requests.
 * Used by users to book banquet halls or private rooms, including custom menu options.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BanquetReservationRequest {

    @NotBlank(message = "Canteen ID cannot be empty")
    private String canteenId;

    @NotBlank(message = "Room ID cannot be empty")
    private String roomId; // 具体包厢的ID

    @NotNull(message = "Event date cannot be null")
    @FutureOrPresent(message = "Event date must be today or in the future")
    private LocalDate eventDate;

    @NotNull(message = "Event time cannot be null")
    private LocalTime eventTime;

    @NotNull(message = "Number of guests cannot be null")
    @Min(value = 1, message = "Number of guests must be at least 1")
    private Integer numberOfGuests;

    @NotBlank(message = "Contact name cannot be empty")
    @Size(max = 100, message = "Contact name cannot exceed 100 characters")
    private String contactName;

    @NotBlank(message = "Contact phone number cannot be empty")
    @Pattern(regexp = "^\\+?[0-9. ()-]{7,25}$", message = "Invalid contact phone number format")
    private String contactPhoneNumber;

    @Size(max = 255, message = "Purpose cannot exceed 255 characters")
    private String purpose;

    // 定制菜单选项：现在是菜品项列表，包含 dishId 和 quantity
    private List<BanquetReservationDishItemDto> selectedDishItems; // 修改：使用 BanquetReservationDishItemDto 列表

    private List<String> selectedPackageIds; // 修正：使用 selectedPackageIds (这里依然是套餐ID，没有数量需求)

    private Boolean hasBirthdayCake;

    @Size(max = 1000, message = "Custom menu request cannot exceed 1000 characters")
    private String customMenuRequest;

    @Size(max = 500, message = "Special requests cannot exceed 500 characters")
    private String specialRequests;
}
