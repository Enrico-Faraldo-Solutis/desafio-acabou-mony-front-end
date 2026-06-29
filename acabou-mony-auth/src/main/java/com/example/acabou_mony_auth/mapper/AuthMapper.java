package com.example.acabou_mony_auth.mapper;

import com.example.acabou_mony_auth.dto.login.LoginResponseDto;
import com.example.acabou_mony_auth.dto.token.TokenResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public LoginResponseDto toLoginResponse(Long usuarioId, String mensagem) {
        return new LoginResponseDto(mensagem, usuarioId);
    }

    public TokenResponseDTO toTokenResponse(String token) {
        return new TokenResponseDTO(token, "Bearer");
    }
}