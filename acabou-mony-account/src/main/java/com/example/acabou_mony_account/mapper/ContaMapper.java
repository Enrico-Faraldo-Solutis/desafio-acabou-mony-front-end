package com.example.acabou_mony_account.mapper;

import com.example.acabou_mony_account.dto.conta.ContaResponseDto;
import com.example.acabou_mony_account.entity.Conta;
import org.springframework.stereotype.Component;

@Component
public class ContaMapper {

    public ContaResponseDto toDto(Conta entity) {
        if (entity == null) {
            return null;
        }

        ContaResponseDto dto = new ContaResponseDto();
        dto.setId(entity.getId());

        if (entity.getUsuario() != null) {
            dto.setUsuarioId(entity.getUsuario().getId());
        }

        dto.setSaldo(entity.getSaldo());
        dto.setStatus(entity.getStatus());
        dto.setDataCriacao(entity.getDataCriacao());

        return dto;
    }

}