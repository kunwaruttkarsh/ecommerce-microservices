package com.ecommerce.productservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "Stock update request")
public class StockUpdateRequest {

    @NotNull
    @Min(value = 1, message = "Quantity must be atleast 1")
    @Schema(example = "3")
    private Integer quantity;
}
