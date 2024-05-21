package com.codeconnect.controller;

import com.codeconnect.service.UsuarioService;
import com.codeconnect.dto.UsuarioResponse;
import com.codeconnect.dto.UsuarioResquest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponse> cadastrar(@RequestBody @Valid UsuarioResquest usuarioResquest) {
        UsuarioResponse usuarioResponse = usuarioService.cadastrar(usuarioResquest);

        return ResponseEntity.status(HttpStatus.CREATED).body(usuarioResponse);
    }

}