package com.example.acabou_mony_auditing.controller;

import com.example.acabou_mony_auditing.dto.AuditLogCreateDTO;
import com.example.acabou_mony_auditing.dto.AuditLogResponseDTO;
import com.example.acabou_mony_auditing.service.AuditingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auditing")
@RequiredArgsConstructor
@Tag(name = "Auditoria", description = "Endpoints para registro e consulta de logs de auditoria imutáveis")
public class AuditingController {

    private final AuditingService service;

    @PostMapping
    @Operation(summary = "Registra uma nova ação crítica (Uso Interno do Squad)")
    public ResponseEntity<AuditLogResponseDTO> registrarLog(@Valid @RequestBody AuditLogCreateDTO dto) {
        AuditLogResponseDTO response = service.registrarLog(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Lista todos os logs de auditoria do sistema de forma paginada")
    public ResponseEntity<Page<AuditLogResponseDTO>> listarTodos(Pageable pageable) {
        return ResponseEntity.ok(service.listarTodos(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca um log de auditoria específico pelo ID")
    public ResponseEntity<AuditLogResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Filtra e lista logs de auditoria por um usuário específico")
    public ResponseEntity<Page<AuditLogResponseDTO>> listarPorUsuario(
            @PathVariable Long usuarioId, Pageable pageable) {
        return ResponseEntity.ok(service.listarPorUsuario(usuarioId, pageable));
    }

    @GetMapping("/acao/{acao}")
    @Operation(summary = "Filtra e lista logs de auditoria por uma ação específica")
    public ResponseEntity<Page<AuditLogResponseDTO>> listarPorAcao(
            @PathVariable String acao, Pageable pageable) {
        return ResponseEntity.ok(service.listarPorAcao(acao, pageable));
    }

    @GetMapping("/entidade")
    @Operation(summary = "Filtra logs de auditoria por nome e ID de uma entidade do banco")
    public ResponseEntity<Page<AuditLogResponseDTO>> listarPorEntidade(
            @RequestParam String nome,
            @RequestParam Long id,
            Pageable pageable) {
        return ResponseEntity.ok(service.listarPorEntidade(nome, id, pageable));
    }
}