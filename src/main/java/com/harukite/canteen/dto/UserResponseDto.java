package com.harukite.canteen.dto;

import com.harukite.canteen.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * DTO for user response.
 * Used to send user details to the client, excluding sensitive information like password hash.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto
{
    private String userId;
    private String username;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private LocalDateTime createdAt;
}
