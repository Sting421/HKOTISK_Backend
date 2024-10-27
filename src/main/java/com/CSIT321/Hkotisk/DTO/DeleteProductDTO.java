package com.CSIT321.Hkotisk.DTO;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class DeleteProductDTO {
    @NotNull(message = "Product ID is mandatory")
    @Positive(message = "Product ID must be positive")
    private Integer productId;
}
