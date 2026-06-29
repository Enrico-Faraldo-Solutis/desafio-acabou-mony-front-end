package com.example.acabou_mony_transaction.dto.conta;

import lombok.*;

import java.math.BigDecimal;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AtualizarSaldoRequestDto {
    private Long contaId;
    private BigDecimal valor;


}
