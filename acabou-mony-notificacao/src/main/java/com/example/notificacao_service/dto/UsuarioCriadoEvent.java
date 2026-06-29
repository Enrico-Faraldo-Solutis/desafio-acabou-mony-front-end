package com.example.notificacao_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioCriadoEvent {
    private Long id;
    private String nome;
    private String email;
    private String cpf;
}