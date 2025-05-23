package com.harukite.canteen.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Dietary Tag information.
 * Used for creating, updating, and retrieving dietary tag details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DietaryTagDto
{
    private String tagId; // For existing tags, might be null for new ones
    @NotBlank(message = "Dietary tag name cannot be blank")
    @Size(max = 100, message = "Dietary tag name cannot exceed 100 characters")
    private String tagName;
}
