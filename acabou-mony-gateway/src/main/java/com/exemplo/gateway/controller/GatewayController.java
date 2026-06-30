package com.exemplo.gateway.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class GatewayController {

    private final WebClient webClient;

    // URLs injetadas do application.properties — sem hardcode de porta
    @Value("${services.auth.url}")
    private String authUrl;

    @Value("${services.account.url}")
    private String accountUrl;

    @Value("${services.card.url}")
    private String cardUrl;

    @Value("${services.transaction.url}")
    private String transactionUrl;

    @Value("${services.auditing.url}")
    private String auditingUrl;

    public GatewayController() {
        this.webClient = WebClient.builder().build();
    }

    @GetMapping("/teste")
    public Mono<String> teste() {
        return Mono.just("Gateway funcionando!");
    }

    @RequestMapping("/{service}/{path:^(?!api).*$}/**")
    public Mono<ResponseEntity<String>> proxy(
            @PathVariable String service,
            @PathVariable String path,
            @RequestHeader HttpHeaders headers,
            @RequestParam(required = false) MultiValueMap<String, String> queryParams,
            @RequestBody(required = false) Mono<String> body,
            ServerHttpRequest request) {

                // Mapeia o segmento da URL para a URL base do microsserviço correspondente
        String baseUrl = switch (service) {
            case "auth"         -> authUrl;
            case "accounts"     -> accountUrl;
            case "users"        -> accountUrl;  // users também vai para account service
            case "cards"        -> cardUrl;
            case "transactions" -> transactionUrl;
            case "auditing"     -> auditingUrl;
            default             -> null;
        };

        if (baseUrl == null) {
            return Mono.just(ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("Serviço não encontrado: " + service));
        }

                        // Extract the full path after /api/ (e.g., /api/auth/login -> /auth/login)
        String fullPath = request.getURI().getRawPath().replace("/api", "");
        
        // Keep the full path including the service name
        String targetPath = fullPath;

        System.out.println("FULL PATH: " + fullPath);
        System.out.println("TARGET PATH: " + targetPath);
        System.out.println("BASE URL: " + baseUrl);

        return webClient.method(request.getMethod())
                .uri(baseUrl + targetPath)
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .body(body == null ? Mono.empty() : body, String.class)
                .retrieve()
                .toEntity(String.class);
    }
}