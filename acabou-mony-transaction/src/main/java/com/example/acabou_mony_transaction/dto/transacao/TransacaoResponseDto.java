package com.example.acabou_mony_transaction.dto.transacao;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for transaction operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransacaoResponseDto {

    @Schema(description = "Transaction ID", example = "1")
    private Long id;

    @Schema(description = "Source account ID", example = "1")
    private Long contaOrigemId;

    @Schema(description = "Destination account ID", example = "2")
    private Long contaDestinoId;

    @Schema(description = "Transaction amount", example = "100.50")
    private BigDecimal valor;

    @Schema(description = "Transaction status (PENDENTE, CONCLUIDA, FALHA)", example = "CONCLUIDA")
    private String status;

    @Schema(description = "Transaction type (DEBITO, CREDITO, TRANSFERENCIA)", example = "TRANSFERENCIA")
    private String tipo;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Schema(description = "Transaction timestamp", example = "2024-01-15T10:30:00")
    private LocalDateTime dataTransacao;
}

