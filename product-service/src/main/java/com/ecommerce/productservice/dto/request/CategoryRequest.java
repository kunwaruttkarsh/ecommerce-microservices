package com.ecommerce.productservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Category create request")
public class CategoryRequest {

    @NotBlank(message = "Name is Required!!")
    @Schema(example = "Electronics")
    private String name;

    @Schema(example = "Phones, laptops and gadgets")
    private String description;
}
