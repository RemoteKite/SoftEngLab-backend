package com.harukite.canteen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO for rating and review responses.
 * Used to display detailed rating and review information to the client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingReviewDto
{
    private String reviewId;
    private String userId;
    private String username; // Reviewer's username
    private String dishId;
    private String dishName; // Reviewed dish's name
    private Integer rating;
    private String comment;
    private LocalDateTime reviewDate;
}