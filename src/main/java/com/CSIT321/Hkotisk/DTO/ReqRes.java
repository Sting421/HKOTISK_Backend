package com.CSIT321.Hkotisk.DTO;

import com.CSIT321.Hkotisk.Entity.StudentEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReqRes implements Serializable {

    private int statusCode;
    private String error;
    private String message;
    private String token;
    private String refreshToken;
    private String expirationTime;
    private String idNumber;
    private String firstName;
    private String lastName;
    private String password;
    private String email;
    private String role;
    private StudentEntity student;
    private List<StudentEntity> studentsList;

}