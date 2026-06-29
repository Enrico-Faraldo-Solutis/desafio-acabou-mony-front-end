package com.example.acabou_mony_account.dto.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioCriadoEvent implements Serializable {
    private Long usuarioId;
    private String nome;
    private String email;
    private String cpf;
}