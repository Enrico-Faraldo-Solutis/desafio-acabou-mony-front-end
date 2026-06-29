package com.example.acabou_mony_card.controller;

import com.example.acabou_mony_card.dto.CardGenerationRequestDTO;
import com.example.acabou_mony_card.dto.CardResponseDTO;
import com.example.acabou_mony_card.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    // Emite um novo cartão para uma conta informada
    @PostMapping
    public ResponseEntity<CardResponseDTO> gerarCartao(@Valid @RequestBody CardGenerationRequestDTO request) {
        CardResponseDTO novoCartao = cardService.gerarCartao(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoCartao);
    }

    // Busca todos os cartões vinculados a um ID de conta específico
    @GetMapping("/account/{contaId}")
    public ResponseEntity<List<CardResponseDTO>> listarCartoesPorConta(@PathVariable Long contaId) {
        List<CardResponseDTO> cartoes = cardService.listarCartoesPorConta(contaId);
        return ResponseEntity.ok(cartoes);
    }

    // Endpoint de segurança para bloquear ou desbloquear o cartão temporariamente
    @PutMapping("/{id}/toggle-status")
    public ResponseEntity<CardResponseDTO> alternarStatusCartao(@PathVariable Long id) {
        CardResponseDTO cartaoAtualizado = cardService.alternarStatusCartao(id);
        return ResponseEntity.ok(cartaoAtualizado);
    }
}