package com.harukite.canteen.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for user login requests.
 * Used to receive login credentials from the client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest
{

    @NotBlank(message = "Username or email cannot be empty")
    private String usernameOrEmail; // Can be username or email

    @NotBlank(message = "Password cannot be empty")
    private String password;
}
