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

    private double[] prices;

    @Min(value = 1, message = "Quantity must be positive")
    private int quantity;

    private String[] sizes;

    @NotBlank(message = "Category is mandatory")
    private String category;

    @Lob
    private byte[] productImage;
}