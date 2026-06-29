package com.example.acabou_mony_card.mapper;

import com.example.acabou_mony_card.dto.CardGenerationRequestDTO;
import com.example.acabou_mony_card.dto.CardResponseDTO;
import com.example.acabou_mony_card.entity.Cartao;
import org.springframework.stereotype.Component;

@Component
public class CartaoMapper {

    public Cartao toEntity(CardGenerationRequestDTO dto) {
        if (dto == null) return null;

        Cartao cartao = new Cartao();
        cartao.setContaId(dto.getContaId());
        cartao.setNomeImpresso(dto.getNomeImpresso().toUpperCase().trim());
        return cartao;
    }

    public CardResponseDTO toDto(Cartao entity) {
        if (entity == null) return null;

        CardResponseDTO dto = new CardResponseDTO();
        dto.setId(entity.getId());
        dto.setContaId(entity.getContaId());
        dto.setNumeroCartao(entity.getNumeroCartao());
        dto.setNomeImpresso(entity.getNomeImpresso());
        dto.setDataValidade(entity.getDataValidade());
        dto.setAtivo(entity.isAtivo());
        return dto;
    }
}