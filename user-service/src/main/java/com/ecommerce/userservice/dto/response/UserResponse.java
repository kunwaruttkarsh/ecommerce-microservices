package com.ecommerce.userservice.dto.response;

import com.ecommerce.userservice.enums.Role;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private Role role;
    private boolean active;
    private LocalDateTime createdAt;
}