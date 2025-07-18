package com.creadev.dto.request.user;

import jakarta.validation.constraints.*;

public record UpdateUserRequest(
    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 255, message = "First name must be between 1 and 255 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "First name can only contain letters and spaces")
    String firstName,

    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 255, message = "Last name must be between 1 and 255 characters")
    @Pattern(regexp = "^[a-zA-Z\\s]+$", message = "Last name can only contain letters and spaces")
    String lastName,

    @Email(message = "Email must be a valid email address")
    @NotBlank(message = "Email is required")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    String email,

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\d{9}", message = "Phone number must be exactly 9 digits (without country code)")
    String phoneNumber,

    @NotNull(message = "Role ID is required")
    Integer roleId
) {
} 