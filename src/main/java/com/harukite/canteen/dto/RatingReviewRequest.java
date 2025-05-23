package com.harukite.canteen.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for rating and review requests.
 * Used by users to submit ratings and comments for dishes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingReviewRequest
{

    @NotBlank(message = "Dish ID cannot be empty")
    private String dishId;

    @NotNull(message = "Rating cannot be null")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot be more than 5")
    private Integer rating;

    @Size(max = 500, message = "Comment cannot exceed 500 characters")
    private String comment;
}
