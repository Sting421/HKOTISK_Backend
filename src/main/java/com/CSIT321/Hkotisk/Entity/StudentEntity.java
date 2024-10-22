package com.CSIT321.Hkotisk.Entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tblstudent")
public class StudentEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sid")
    private int sid;

    @NotEmpty(message = "ID number is required.")
    @Column(name = "id_number")
    @JsonProperty
    private String idNumber;

    @NotEmpty(message = "Email is required.")
    @JsonProperty
    private String email;

    @NotEmpty(message = "First name is required.")
    @JsonProperty
    private String firstName;

    @NotEmpty(message = "Last name is required.")
    @JsonProperty
    private String lastName;

    @Column(name = "password")
    @NotEmpty(message = "Password is required.")
    @JsonProperty
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_type")
    private UserType userType = UserType.STUDENT;

    @Column(name = "is_deleted")
    private boolean isDeleted;

    @OneToMany(mappedBy = "student")
    @JsonIgnoreProperties({"student"})
    private List<OrderEntity> order;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(userType.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return idNumber;
    }
}
