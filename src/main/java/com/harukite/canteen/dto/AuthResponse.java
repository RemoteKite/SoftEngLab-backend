package com.harukite.canteen.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for authentication responses.
 * Used to send authentication results (e.g., JWT token, user info) back to the client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse
{
    private String userId;
    private String username;
    private String role;
    private String token; // JWT token
    private String message;
}
