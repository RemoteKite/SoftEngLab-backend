package com.harukite.canteen.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for Canteen Image information.
 * Used for creating, updating, and displaying additional canteen images.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CanteenImageDto {
    private String imageId; // 对于现有图片，可能为 null
    @NotBlank(message = "Canteen ID cannot be empty")
    private String canteenId; // 所属食堂ID
    @NotBlank(message = "Image URL cannot be empty")
    private String imageUrl; // 图片URL
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description; // 图片描述
    private LocalDateTime uploadTime; // 上传时间
}
