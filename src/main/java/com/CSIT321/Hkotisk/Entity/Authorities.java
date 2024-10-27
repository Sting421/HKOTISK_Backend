package com.CSIT321.Hkotisk.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.io.Serializable;

@Entity
@Data
@ToString
public class Authorities implements Serializable {
    private static final long serialVersionUID = 6005072159059903199L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "AUTHORITY_ID")
    private int authorityId;

    @NotBlank(message = "Username is mandatory")
    private String username;

    @NotBlank(message = "Authority is mandatory")
    private String authority;

    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username", insertable = false, updatable = false)
    private User user;
}