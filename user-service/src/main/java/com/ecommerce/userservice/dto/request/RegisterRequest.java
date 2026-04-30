package com.ecommerce.userservice.dto.request;

import com.ecommerce.userservice.enums.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "Register request")
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    @Schema(example = "Rahul Sharma")
    private String name;

    @Email(message = "Enter a valid email")
    @NotBlank(message = "Email is required")
    @Schema(example = "rahul@customer.com")
    private String email;

    @Size(min = 6, message = "Password min 6 characters")
    @Schema(example = "rahul123")
    private String password;

    @Schema(example = "9876543210")
    private String phone;

    @Schema(example = "Mumbai, Maharashtra")
    private String address;

    @NotNull(message = "Role is required")
    @Schema(example = "CUSTOMER")
    private Role role;
}