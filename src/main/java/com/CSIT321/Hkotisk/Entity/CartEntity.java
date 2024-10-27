package com.CSIT321.Hkotisk.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;


import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "cart")
@Data
@ToString
public class CartEntity implements Serializable {
    private static final long serialVersionUID = 4049687597028261161L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private int cartId;

    @Column(name = "order_id", nullable = true)
    private int orderId;

    private String email;

    @Column(name = "date_added")
    private Date dateAdded;

    private int quantity;
    private double price;
    @Column(name = "product_id")
    private int productId;

    private String productName;

}