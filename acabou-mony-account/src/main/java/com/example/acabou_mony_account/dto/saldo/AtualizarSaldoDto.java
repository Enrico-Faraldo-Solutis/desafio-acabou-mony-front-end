package com.example.acabou_mony_account.dto.saldo;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AtualizarSaldoDto {

    @NotNull(message = "O ID da conta é obrigatório")
    private Long contaId;

    @NotNull(message = "O valor para atualização é obrigatório")
    private BigDecimal valor;

}