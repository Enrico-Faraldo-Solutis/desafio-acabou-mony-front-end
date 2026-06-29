package com.example.acabou_mony_auth.dto.login;

public class LoginResponseDto {

    private String mensagem;
    private Long usuarioId;

    public LoginResponseDto() {
    }

    public LoginResponseDto(String mensagem, Long usuarioId) {
        this.mensagem = mensagem;
        this.usuarioId = usuarioId;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
}