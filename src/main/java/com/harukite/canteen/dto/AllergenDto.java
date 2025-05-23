package com.harukite.canteen.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Allergen information.
 * Used for creating, updating, and retrieving allergen details.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AllergenDto
{
    private String allergenId; // For existing allergens, might be null for new ones
    @NotBlank(message = "Allergen name cannot be blank")
    @Size(max = 100, message = "Allergen name cannot exceed 100 characters")
    private String allergenName;
}
