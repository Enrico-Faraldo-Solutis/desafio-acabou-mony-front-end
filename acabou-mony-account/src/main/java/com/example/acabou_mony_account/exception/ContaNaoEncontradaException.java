package com.example.acabou_mony_account.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ContaNaoEncontradaException extends RuntimeException {
    public ContaNaoEncontradaException() {
        super("Conta não foi encontrada");
    }
}
