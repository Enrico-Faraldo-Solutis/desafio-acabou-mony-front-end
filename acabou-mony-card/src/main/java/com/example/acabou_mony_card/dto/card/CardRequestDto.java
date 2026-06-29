package com.example.acabou_mony_card.dto.card;

public class CardRequestDto {
    private Long contaId;
    private String tipo;
    private String status;

    public CardRequestDto() {}

    public CardRequestDto(Long contaId, String tipo, String status) {
        this.contaId = contaId;
        this.tipo = tipo;
        this.status = status;
    }

    public Long getContaId() {
        return contaId;
    }

    public void setContaId(Long contaId) {
        this.contaId = contaId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}