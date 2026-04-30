package com.ecommerce.userservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "Login request")
public class LoginRequest {

    @Schema(example = "rahul@customer.com")
    private String email;

    @Schema(example = "rahul123")
    private String password;
}