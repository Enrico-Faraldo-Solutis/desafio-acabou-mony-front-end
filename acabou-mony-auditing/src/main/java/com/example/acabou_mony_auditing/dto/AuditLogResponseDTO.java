package com.example.acabou_mony_auditing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogResponseDTO {

    private Long id;
    private Long usuarioId;
    private String acao;
    private String entidadeNome;
    private Long entidadeId;
    private Map<String, Object> detalhes;
    private String ipOrigem;
    private LocalDateTime timestamp;
}