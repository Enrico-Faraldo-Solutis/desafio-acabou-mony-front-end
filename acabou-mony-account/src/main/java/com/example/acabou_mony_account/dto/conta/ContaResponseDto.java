package com.example.acabou_mony_account.dto.conta;

import com.example.acabou_mony_account.entity.StatusConta;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ContaResponseDto {

    private Long id;
    private Long usuarioId;
    private BigDecimal saldo;
    private StatusConta status;
    private LocalDateTime dataCriacao;
}