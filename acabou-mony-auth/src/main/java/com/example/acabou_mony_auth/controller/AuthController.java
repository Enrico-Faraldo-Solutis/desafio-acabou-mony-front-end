package com.example.acabou_mony_auth.controller;

import com.example.acabou_mony_auth.dto.login.LoginRequestDto;
import com.example.acabou_mony_auth.dto.login.LoginResponseDto;
import com.example.acabou_mony_auth.dto.verify2fa.Verify2FARequestDTO;
import com.example.acabou_mony_auth.dto.token.TokenResponseDTO;
import com.example.acabou_mony_auth.mapper.AuthMapper;
import com.example.acabou_mony_auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthMapper authMapper;

    public AuthController(AuthService authService, AuthMapper authMapper) {
        this.authService = authService;
        this.authMapper = authMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        Long usuarioId = authService.processarLoginIncial(request.getEmail(), request.getSenha());
        LoginResponseDto response = authMapper.toLoginResponse(usuarioId, "Credenciais válidas. Código 2FA enviado.");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-2fa")
    public ResponseEntity<TokenResponseDTO> verify2FA(@RequestBody Verify2FARequestDTO request) {
        String token = authService.validar2FAeGerarToken(request.getUsuarioId(), request.getCodigo());
        TokenResponseDTO response = authMapper.toTokenResponse(token);
        return ResponseEntity.ok(response);
    }
}