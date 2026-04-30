package com.ecommerce.security_lib.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticatedUser {

    private Long   userId;
    private String email;
    private String role;

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public boolean isSeller() {
        return "SELLER".equals(role);
    }

    public boolean isCustomer() {
        return "CUSTOMER".equals(role);
    }
}