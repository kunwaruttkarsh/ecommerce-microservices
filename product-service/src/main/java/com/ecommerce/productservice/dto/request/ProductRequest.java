package com.ecommerce.productservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

@Data
@Schema(description = "Product create/update request")
public class ProductRequest {

    @NotBlank(message = "Name is required")
    @Schema(example = "iPhone 15 Pro")
    private String name;

    @Schema(example = "Latest Apple iPhone")
    private String description;

    @NotNull(message = "Price is Required")
    @Min(value = 0, message = "Price must be positive")
    @Schema(example = "134900")
    private Double price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock must be positive")
    @Schema(example = "25")
    private Integer stock;

    @Schema(example = "https://example.com/iphone.jpg")
    private String imageUrl;

    @NotNull(message = "Category is required")
    @Schema(example = "1")
    private Long categoryId;
}
