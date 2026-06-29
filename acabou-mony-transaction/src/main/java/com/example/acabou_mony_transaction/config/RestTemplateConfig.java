package com.example.acabou_mony_transaction.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * RestTemplate configuration with timeout and error handling
 * Connection timeout: 5 seconds
 * Read timeout: 10 seconds
 */
@Configuration
@Slf4j
public class RestTemplateConfig {

    private static final int CONNECTION_TIMEOUT_MS = 5000;
    private static final int READ_TIMEOUT_MS = 10000;

    /**
     * Creates a RestTemplate bean with configured timeouts
     * 
     * @param builder the RestTemplateBuilder
     * @return configured RestTemplate instance
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        log.info("Configuring RestTemplate with connection timeout: {}ms, read timeout: {}ms",
                CONNECTION_TIMEOUT_MS, READ_TIMEOUT_MS);
        
        return builder
                .setConnectTimeout(Duration.ofMillis(CONNECTION_TIMEOUT_MS))
                .setReadTimeout(Duration.ofMillis(READ_TIMEOUT_MS))
                .requestFactory(this::clientHttpRequestFactory)
                .build();
    }

    /**
     * Creates a ClientHttpRequestFactory with configured timeouts
     * 
     * @return configured ClientHttpRequestFactory
     */
    private ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(CONNECTION_TIMEOUT_MS);
        factory.setReadTimeout(READ_TIMEOUT_MS);
        factory.setBufferRequestBody(true);
        
        log.debug("ClientHttpRequestFactory configured with timeouts");
        return factory;
    }
}
