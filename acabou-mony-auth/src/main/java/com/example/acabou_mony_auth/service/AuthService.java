package com.example.acabou_mony_auth.service;

import com.example.acabou_mony_auth.exception.AuthException;
import com.example.acabou_mony_auth.repository.UsuarioRepository;
import com.example.acabou_mony_auth.entity.Usuario;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final TwoFactorAuthService twoFactorAuthService;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public AuthService(TwoFactorAuthService twoFactorAuthService, JwtService jwtService, UsuarioRepository usuarioRepository) {
        this.twoFactorAuthService = twoFactorAuthService;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    public Long processarLoginIncial(String email, String senha) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException("E-mail ou senha inválidos."));


        if (!org.springframework.security.crypto.bcrypt.BCrypt.checkpw(senha, usuario.getSenhaHash())) {
            throw new AuthException("E-mail ou senha inválidos.");
        }

        Long usuarioId = usuario.getId();
        twoFactorAuthService.gerarEEnviarCodigo(usuarioId, email);

        return usuarioId;
    }

    public String validar2FAeGerarToken(Long usuarioId, String codigo) {
        twoFactorAuthService.validarCodigo(usuarioId, codigo);

        String emailUsuario = usuarioRepository.findById(usuarioId)
                .map(Usuario::getEmail)
                .orElseThrow(() -> new AuthException("Usuário não encontrado."));

        return jwtService.gerarToken(usuarioId, emailUsuario);
    }
}