package com.CSIT321.Hkotisk.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.io.Serializable;

@Entity
@Table(name = "Product")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntity implements Serializable {
    private static final long serialVersionUID = -7446162716367847201L;



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int productId;

    @NotBlank(message = "Description is mandatory")
    private String description;

    @NotBlank(message = "Product name is mandatory")
    private String productName;

    @Positive(message = "Price must be positive")
    private double price;

    @Min(value = 0, message = "Quantity must be zero or positive")
    private int quantity;

    @NotBlank(message = "Size is mandatory")
    @Enumerated(EnumType.STRING)
    private String size;

    @Lob
    private byte[] productImage;
}