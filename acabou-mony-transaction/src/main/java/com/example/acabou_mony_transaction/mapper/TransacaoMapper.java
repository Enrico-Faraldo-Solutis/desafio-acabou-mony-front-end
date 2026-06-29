package com.example.acabou_mony_transaction.mapper;

import com.example.acabou_mony_transaction.dto.transacao.TransacaoResquestDto;
import com.example.acabou_mony_transaction.dto.transacao.TransacaoResponseDto;
import com.example.acabou_mony_transaction.entity.Transacao;
import org.springframework.stereotype.Component;

@Component
public class TransacaoMapper {

    /**
     * Converte TransacaoResquestDto para Transacao entity
     *
     * @param dto o DTO de requisição
     * @return a entidade Transacao
     */
    public Transacao toEntity(TransacaoResquestDto dto) {
        if (dto == null) {
            return null;
        }

        return Transacao.builder()
                .contaOrigemId(dto.getContaOrigemId())
                .contaDestinoId(dto.getContaDestinoId())
                .valor(dto.getValor())
                .tipo(Transacao.TipoTransacao.valueOf(dto.getTipo().toUpperCase()))
                .status(Transacao.StatusTransacao.PENDENTE)
                .build();
    }

    /**
     * Converte Transacao entity para TransacaoResponseDto
     *
     * @param entity a entidade Transacao
     * @return o DTO de resposta
     */
    public TransacaoResponseDto toResponseDto(Transacao entity) {
        if (entity == null) {
            return null;
        }

        return TransacaoResponseDto.builder()
                .id(entity.getId())
                .contaOrigemId(entity.getContaOrigemId())
                .contaDestinoId(entity.getContaDestinoId())
                .valor(entity.getValor())
                .status(entity.getStatus().name())
                .tipo(entity.getTipo().name())
                .dataTransacao(entity.getDataTransacao())
                .build();
    }
}
