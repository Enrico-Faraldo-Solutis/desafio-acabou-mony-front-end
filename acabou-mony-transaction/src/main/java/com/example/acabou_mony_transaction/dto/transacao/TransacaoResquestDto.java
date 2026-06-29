package com.example.acabou_mony_transaction.dto.transacao;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Request DTO for creating a new transaction
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransacaoResquestDto {

    @NotNull(message = "Conta de origem é obrigatória")
    @Schema(description = "Source account ID", example = "1", required = true)
    private Long contaOrigemId;

    @NotNull(message = "Conta de destino é obrigatória")
    @Schema(description = "Destination account ID", example = "2", required = true)
    private Long contaDestinoId;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    @Schema(description = "Transaction amount", example = "100.50", required = true)
    private BigDecimal valor;

    @NotNull(message = "Tipo de transação é obrigatório")
    @Schema(description = "Transaction type (DEBITO, CREDITO, TRANSFERENCIA)", example = "TRANSFERENCIA", required = true)
    private String tipo;
}

