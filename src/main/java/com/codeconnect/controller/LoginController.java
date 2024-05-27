package com.codeconnect.controller;

import com.codeconnect.login.dto.LoginRequest;
import com.codeconnect.login.dto.LoginResponse;
import com.codeconnect.login.service.LoginService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @PostMapping
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        LoginResponse token = loginService.autenticarUsuario(loginRequest);

        return ResponseEntity.ok(token);
    }

}