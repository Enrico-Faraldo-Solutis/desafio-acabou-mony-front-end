package com.example.acabou_mony_account.controller;

import com.example.acabou_mony_account.dto.conta.ContaResponseDto;
import com.example.acabou_mony_account.dto.saldo.AtualizarSaldoDto;
import com.example.acabou_mony_account.service.ContaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class ContaController {

    private final ContaService contaService;

    @GetMapping("/balance/{contaId}")
    public ResponseEntity<ContaResponseDto> consultarConta(@PathVariable Long contaId) {
        ContaResponseDto conta = contaService.consultarConta(contaId);
        return ResponseEntity.ok(conta);
    }

    @PutMapping("/balance")
    public ResponseEntity<ContaResponseDto> atualizarSaldo(@Valid @RequestBody AtualizarSaldoDto dto) {
        ContaResponseDto contaAtualizada = contaService.atualizarSaldo(dto);
        return ResponseEntity.ok(contaAtualizada);
    }

    @GetMapping
    public ResponseEntity<Page<ContaResponseDto>> listarTodasContas(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {

        Page<ContaResponseDto> contas = contaService.listarTodasContas(pageable);
        return ResponseEntity.ok(contas);
    }
}