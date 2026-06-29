package com.example.acabou_mony_account.dto.conta;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContaRequestDto {

    @NotNull(message = "O ID do usuário é obrigatório para criar uma conta")
    private Long usuarioId;

}