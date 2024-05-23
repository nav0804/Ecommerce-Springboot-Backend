package com.ecommerce.springboot.models;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CartDto {

    private Integer productId;
    private String productName;
    private Double price;

    @Min(1)
    private Integer quantity;
}
