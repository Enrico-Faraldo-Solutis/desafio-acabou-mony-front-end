package com.example.acabou_mony_transaction.client;

import com.example.acabou_mony_transaction.dto.auditoria.AuditLogDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Client for communicating with the Auditing Service
 * Logs critical transaction operations asynchronously
 * Failures do NOT block transaction processing (fire-and-forget pattern)
 */
@Component
@Slf4j
public class AuditingClient {

    @Value("${auditing.service.url:http://localhost:8084}")
    private String auditingServiceUrl;

    private final RestTemplate restTemplate;

    public AuditingClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Logs a transaction audit entry to the auditing service
     * Failures are logged but do NOT throw exceptions (fire-and-forget)
     *
     * @param auditLog the audit log entry to record
     */
    public void logTransaction(AuditLogDto auditLog) {
        try {
            String url = auditingServiceUrl + "/api/auditing";
            log.info("Sending audit log to auditing service: transacaoId={}, status={}", 
                    auditLog.getTransacaoId(), auditLog.getStatus());
            
            restTemplate.postForObject(url, auditLog, Void.class);
            
            log.info("Audit log sent successfully: transacaoId={}", auditLog.getTransacaoId());
        } catch (RestClientException e) {
            log.error("Failed to send audit log to auditing service: {}. Error: {}", 
                    auditLog.getTransacaoId(), e.getMessage());
            // Do NOT rethrow - auditing failures should not block transaction processing
        } catch (Exception e) {
            log.error("Unexpected error while sending audit log: {}", e.getMessage(), e);
            // Do NOT rethrow - auditing failures should not block transaction processing
        }
    }
}
