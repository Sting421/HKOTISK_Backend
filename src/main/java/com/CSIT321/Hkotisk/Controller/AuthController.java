package com.CSIT321.Hkotisk.Controller;

import com.CSIT321.Hkotisk.DTO.ReqRes;
import com.CSIT321.Hkotisk.Service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/auth")
@RestController
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping(path = "/signup")
    public ResponseEntity<ReqRes> registerUser(@Valid @RequestBody ReqRes input) {
        return authService.register(input);
    }

    @PostMapping(path = "/signin")
    public ResponseEntity<ReqRes> loginStudent(@Valid @RequestBody ReqRes input) {
        return authService.login(input);
    }

}