package com.example.acabou_mony_auth.dto.validacao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Validacao2FARequestDTO {
    private Long usuarioId;
    private String codigo;
}