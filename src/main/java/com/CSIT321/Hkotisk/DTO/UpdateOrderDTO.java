package com.CSIT321.Hkotisk.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateOrderDTO {

    @NotNull(message = "Order ID is mandatory")
    private Integer orderId;

    @NotBlank(message = "Order status is mandatory")
    private String orderStatus;
}