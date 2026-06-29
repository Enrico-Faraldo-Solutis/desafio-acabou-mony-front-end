package com.example.acabou_mony_card.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CartaoNaoEncontradoException extends RuntimeException {
    public CartaoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}