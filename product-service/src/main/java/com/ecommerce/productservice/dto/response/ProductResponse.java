package com.ecommerce.productservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProductResponse {

    private Long id;
    private String name;
    private String description;
    private Double price;
    private Integer stock;
    private String imageUrl;
    private String categoryName;
    private Long categoryId;
    private Long sellerId;
    private boolean active;
    private LocalDateTime createdAt;

}
