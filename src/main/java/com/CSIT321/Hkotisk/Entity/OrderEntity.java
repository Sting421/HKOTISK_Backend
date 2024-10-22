package com.CSIT321.Hkotisk.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tblorder")
public class OrderEntity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private int orderId;

    @Column(name = "contact_information")
    private String contactInfo;

    private String location;

    private double price;

    private double totalAmount;

    private String foodName;

    private int quantity;

    @Column(name = "payment_method")
    private String paymentMethod;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sid", referencedColumnName = "sid", unique = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private StudentEntity student;
}
