package com.example.acabou_mony_transaction.client;

import com.example.acabou_mony_transaction.dto.conta.AtualizarSaldoRequestDto;
import com.example.acabou_mony_transaction.dto.conta.ContaEspelhoDto;
import com.example.acabou_mony_transaction.exception.AccountServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
public class AccountClient {

    @Value("${account.service.url}")
    private String accountServiceUrl;

    private final RestTemplate restTemplate;

    public AccountClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Verifica o saldo de uma conta específica com retry logic e circuit breaker
     * Retry: 4 attempts with exponential backoff (100ms, 200ms, 400ms, 800ms)
     * Circuit Breaker: Opens after 30% failure rate, half-open after 30 seconds
     *
     * @param contaId o ID da conta
     * @return os dados da conta com saldo
     */
    @Retry(name = "accountService")
    @CircuitBreaker(name = "accountService", fallbackMethod = "getBalanceFallback")
    public ContaEspelhoDto getBalance(Long contaId) {
        log.info("Fetching balance for account: {}", contaId);
        String url = accountServiceUrl + "/api/accounts/balance/" + contaId;
        try {
            ContaEspelhoDto conta = restTemplate.getForObject(url, ContaEspelhoDto.class);
            log.info("Balance fetched successfully for account: {}", contaId);
            return conta;
        } catch (RestClientException e) {
            log.error("Failed to fetch balance for account: {}", contaId, e);
            throw new AccountServiceUnavailableException("Account service unavailable", e);
        }
    }

    /**
     * Fallback method for getBalance when circuit breaker is open
     *
     * @param contaId o ID da conta
     * @param ex a exceção que acionou o fallback
     * @return nunca retorna (sempre lança exceção)
     * @throws AccountServiceUnavailableException sempre
     */
    public ContaEspelhoDto getBalanceFallback(Long contaId, Exception ex) {
        log.error("Circuit breaker fallback triggered for account: {}. Cause: {}", contaId, ex.getMessage());
        throw new AccountServiceUnavailableException("Account service is currently unavailable. Please try again later.", ex);
    }

    /**
     * Atualiza o saldo de uma conta com retry logic e circuit breaker
     * Retry: 4 attempts with exponential backoff (100ms, 200ms, 400ms, 800ms)
     * Circuit Breaker: Opens after 30% failure rate, half-open after 30 seconds
     *
     * @param contaId o ID da conta
     * @param contaEspelhoDto os dados da conta com novo saldo
     * @return os dados atualizados da conta
     */
    @Retry(name = "accountService")
    @CircuitBreaker(name = "accountService", fallbackMethod = "updateBalanceFallback")
    public ContaEspelhoDto updateBalance(Long contaId, ContaEspelhoDto contaEspelhoDto) {
        log.info("Updating balance for account: {}", contaId);
        String url = accountServiceUrl + "/api/accounts/balance";
        try {
            // 1. Monta o payload com as propriedades 'contaId' e 'valor' que o Account espera
            // Nota: certifique-se se 'valor' lá no Account significa o NOVO SALDO (contaEspelhoDto.getSaldo())
            // ou se significa o VALOR DA TRANSAÇÃO (o delta a ser somado/subtraído).
            AtualizarSaldoRequestDto payload = new AtualizarSaldoRequestDto(
                    contaId,
                    contaEspelhoDto.getSaldo()
            );

            // 2. Coloca o payload correto na entidade da requisição
            org.springframework.http.HttpEntity<AtualizarSaldoRequestDto> requestEntity = new org.springframework.http.HttpEntity<>(payload);

            // 3. Dispara o PUT
            org.springframework.http.ResponseEntity<ContaEspelhoDto> response = restTemplate.exchange(
                    url,
                    org.springframework.http.HttpMethod.PUT,
                    requestEntity,
                    ContaEspelhoDto.class
            );

            ContaEspelhoDto updated = response.getBody();
            log.info("Balance updated successfully for account: {}", contaId);
            return updated;
        } catch (RestClientException e) {
            log.error("Failed to update balance for account: {}", contaId, e);
            throw new AccountServiceUnavailableException("Account service unavailable", e);
        }
    }
    /**
     * Fallback method for updateBalance when circuit breaker is open
     *
     * @param contaId o ID da conta
     * @param contaEspelhoDto os dados da conta
     * @param ex a exceção que acionou o fallback
     * @return nunca retorna (sempre lança exceção)
     * @throws AccountServiceUnavailableException sempre
     */
    public ContaEspelhoDto updateBalanceFallback(Long contaId, ContaEspelhoDto contaEspelhoDto, Exception ex) {
        log.error("Circuit breaker fallback triggered for account update: {}. Cause: {}", contaId, ex.getMessage());
        throw new AccountServiceUnavailableException("Account service is currently unavailable. Please try again later.", ex);
    }
}

