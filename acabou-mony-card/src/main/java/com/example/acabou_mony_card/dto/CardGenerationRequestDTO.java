package com.example.acabou_mony_card.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CardGenerationRequestDTO {

    @NotNull(message = "O ID da conta é obrigatório.")
    private Long contaId;

    @NotBlank(message = "O nome impresso no cartão é obrigatório.")
    private String nomeImpresso;
}