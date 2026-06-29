package com.example.acabou_mony_transaction.dto.auditoria;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for audit log entries
 * Sent to auditing service for critical transaction operations
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogDto {

    private Long transacaoId;

    private Long contaOrigemId;

    private Long contaDestinoId;

    private BigDecimal valor;

    private String status;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private String operacao;

    private String descricao;
}
