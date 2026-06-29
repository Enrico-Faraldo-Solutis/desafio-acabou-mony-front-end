package com.example.acabou_mony_account.dto.usuario;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UsuarioResponseDto {

    private Long id;
    private String nome;
    private String email;
    private String cpf;
    private LocalDateTime dataCriacao;
}