package com.krish.automessaging.datamodel.record;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * The type User request record.
 */
public record UserRequestRecord(String id, String name,
        @Size(min = 4, max = 15, message = "Username must be between 4 to 15 letters") String username,
        @Size(min = 8, message = "Password should be minimum of 8 letters") @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$", message = "Password should match the required criteria") String password,
        @Email(message = "Valid Email address is required for registration") String email,
        @NotBlank(message = "Valid Phone number is required for registration") String phone) {
}
