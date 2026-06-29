package com.example.acabou_mony_card.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CardResponseDTO {
    private Long id;
    private Long contaId;
    private String numeroCartao;
    private String nomeImpresso;
    private LocalDate dataValidade;
    private boolean ativo;
}