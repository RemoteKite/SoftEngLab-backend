package com.harukite.canteen.dto;

import com.harukite.canteen.model.BanquetStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * DTO for banquet reservation responses.
 * Used to display detailed banquet reservation information to the client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BanquetReservationResponse {
    private String banquetId;
    private String userId;
    private String username; // User's username for display
    private String canteenId;
    private String canteenName; // Canteen's name for display
    private String roomId;
    private String roomName; // Room's name for display
    private LocalDate eventDate;
    private LocalTime eventTime;
    private Integer numberOfGuests;
    private String contactName;
    private String contactPhoneNumber;
    private String purpose;
    private String customMenuRequest;
    private Boolean hasBirthdayCake;
    private String specialRequests;
    private BigDecimal totalPrice; // 新增：宴会总价
    private BanquetStatus status;
    private LocalDateTime confirmationDate;
    private LocalDateTime createdAt;
    private List<BanquetReservationDishItemDto> customDishItems; // 修改：列表类型改为 BanquetReservationDishItemDto
    private List<String> packageIds; // List of package IDs selected
    private List<PackageDto> packageDtos; // List of detailed Package DTOs for selected packages
}

