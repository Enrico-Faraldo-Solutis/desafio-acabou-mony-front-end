package com.example.acabou_mony_auditing.mapper;

import com.example.acabou_mony_auditing.dto.AuditLogCreateDTO;
import com.example.acabou_mony_auditing.dto.AuditLogResponseDTO;
import com.example.acabou_mony_auditing.entity.AuditLog;
import org.springframework.stereotype.Component;

@Component
public class AuditingMapper {

    /**
     * Converte o DTO de criação recebido na Controller para a Entidade que será salva no banco.
     * O campo 'timestamp' não é mapeado aqui porque o Hibernate o gerará automaticamente.
     */
    public AuditLog toEntity(AuditLogCreateDTO dto) {
        if (dto == null) {
            return null;
        }

        return AuditLog.builder()
                .usuarioId(dto.getUsuarioId())
                .acao(dto.getAcao())
                .entidadeNome(dto.getEntidadeNome())
                .entidadeId(dto.getEntidadeId())
                .detalhes(dto.getDetalhes())
                .ipOrigem(dto.getIpOrigem())
                .build();
    }

    /**
     * Converte a Entidade vinda do banco de dados para o DTO de resposta que será enviado na API.
     */
    public AuditLogResponseDTO toResponseDTO(AuditLog entity) {
        if (entity == null) {
            return null;
        }

        return AuditLogResponseDTO.builder()
                .id(entity.getId())
                .usuarioId(entity.getUsuarioId())
                .acao(entity.getAcao())
                .entidadeNome(entity.getEntidadeNome())
                .entidadeId(entity.getEntidadeId())
                .detalhes(entity.getDetalhes())
                .ipOrigem(entity.getIpOrigem())
                .timestamp(entity.getTimestamp())
                .build();
    }
}