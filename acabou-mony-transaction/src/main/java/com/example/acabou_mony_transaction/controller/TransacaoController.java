package com.example.acabou_mony_transaction.controller;

import com.example.acabou_mony_transaction.dto.transacao.TransacaoResquestDto;
import com.example.acabou_mony_transaction.dto.transacao.TransacaoResponseDto;
import com.example.acabou_mony_transaction.exception.ErrorResponse;
import com.example.acabou_mony_transaction.service.TransacaoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for transaction operations
 * All exceptions are handled by GlobalExceptionHandler
 */
@RestController
@RequestMapping("/transactions")
@Slf4j
@Validated
@Tag(name = "Transactions", description = "APIs for managing financial transactions")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    /**
     * Processa uma nova transação
     *
     * @param dto os dados da transação
     * @return a transação processada
     */
    @PostMapping
    @Operation(summary = "Create a new transaction",
            description = "Processes a financial transaction including balance validation, " +
                    "account updates, and audit logging")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transaction created successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransacaoResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input or insufficient balance",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "503", description = "Account service unavailable",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TransacaoResponseDto> criarTransacao(
            @Valid @RequestBody TransacaoResquestDto dto) {
        log.info("Recebendo requisição para criar transação: origem={}, destino={}, valor={}",
                dto.getContaOrigemId(), dto.getContaDestinoId(), dto.getValor());

        TransacaoResponseDto transacao = transacaoService.processarTransacao(dto);

        log.info("Transação criada com sucesso: id={}", transacao.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(transacao);
    }

    /**
     * Recupera uma transação pelo ID
     *
     * @param id o ID da transação
     * @return a transação encontrada
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get transaction by ID",
            description = "Retrieves a specific transaction by its unique identifier")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transaction found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransacaoResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "Transaction not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<TransacaoResponseDto> obterTransacao(
            @Parameter(description = "Transaction ID", required = true, example = "1")
            @PathVariable Long id) {
        log.info("Recebendo requisição para obter transação com ID: {}", id);

        TransacaoResponseDto transacao = transacaoService.obterTransacao(id);

        log.info("Transação obtida com sucesso: id={}", id);
        return ResponseEntity.ok(transacao);
    }

    /**
     * Recupera todas as transações de uma conta
     *
     * @param contaId o ID da conta
     * @return lista de transações
     */
    @GetMapping("/conta/{contaId}")
    @Operation(summary = "Get transactions by account ID",
            description = "Retrieves all transactions (incoming and outgoing) for a specific account")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransacaoResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<TransacaoResponseDto>> obterTransacoesPorConta(
            @Parameter(description = "Account ID", required = true, example = "1")
            @PathVariable Long contaId) {
        log.info("Recebendo requisição para obter transações da conta: {}", contaId);

        List<TransacaoResponseDto> transacoes = transacaoService.obterTransacoesPorConta(contaId);

        log.info("Transações obtidas com sucesso: conta={}, quantidade={}", contaId, transacoes.size());
            return ResponseEntity.ok(transacoes);
        }

    /**
     * Recupera todas as transações
     *
     * @return lista de todas as transações
     */
    @GetMapping
    @Operation(summary = "Get all transactions",
            description = "Retrieves all transactions in the system")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All transactions retrieved successfully",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = TransacaoResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<List<TransacaoResponseDto>> obterTodasAsTransacoes() {
        log.info("Recebendo requisição para obter todas as transações");

        List<TransacaoResponseDto> transacoes = transacaoService.obterTodasAsTransacoes();

        log.info("Todas as transações obtidas com sucesso: quantidade={}", transacoes.size());
        return ResponseEntity.ok(transacoes);
}
}

