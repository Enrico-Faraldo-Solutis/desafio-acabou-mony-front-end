package com.example.acabou_mony_card.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

// O parâmetro 'url' aponta para onde o microsserviço de contas está rodando.
@FeignClient(name = "accountClient", url = "http://localhost:8080/api/accounts")
public interface AccountClient {

    // Faz um GET real para o outro microsserviço
    @GetMapping("/balance/{contaId}")
    Object verificarContaExistente(@PathVariable("contaId") Long contaId);

    /* * Dica: Usamos Object (ou um DTO genérico) como retorno porque
     * o CardService não precisa saber o saldo ou os dados da conta.
     * Ele só precisa saber se o endpoint retorna 200 (OK) ou 404 (Not Found).
     */
}