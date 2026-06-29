package com.example.acabou_mony_auditing.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class AuditingNaoEncontradoException extends RuntimeException {

    public AuditingNaoEncontradoException(String message) {
        super(message);
    }
}