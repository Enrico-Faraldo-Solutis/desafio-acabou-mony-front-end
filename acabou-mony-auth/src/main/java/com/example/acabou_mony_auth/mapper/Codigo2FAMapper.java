package com.example.acabou_mony_auth.mapper;

import com.example.acabou_mony_auth.dto.validacao.Validacao2FARequestDTO;
import com.example.acabou_mony_auth.entity.Codigo2FA;
import org.springframework.stereotype.Component;

@Component
public class Codigo2FAMapper {

    public Validacao2FARequestDTO toDTO(Codigo2FA entity) {
        if (entity == null) {
            return null;
        }

        Validacao2FARequestDTO dto = new Validacao2FARequestDTO();
        dto.setUsuarioId(entity.getUsuarioId());
        dto.setCodigo(entity.getCodigo());
        return dto;
    }

    public Codigo2FA toEntity(Validacao2FARequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Codigo2FA entity = new Codigo2FA();
        entity.setUsuarioId(dto.getUsuarioId());
        entity.setCodigo(dto.getCodigo());
        return entity;
    }
}