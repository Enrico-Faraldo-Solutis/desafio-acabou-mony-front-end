package com.example.acabou_mony_auditing.service;

import com.example.acabou_mony_auditing.dto.AuditLogCreateDTO;
import com.example.acabou_mony_auditing.dto.AuditLogResponseDTO;
import com.example.acabou_mony_auditing.entity.AuditLog;
import com.example.acabou_mony_auditing.exception.AuditingNaoEncontradoException;
import com.example.acabou_mony_auditing.mapper.AuditingMapper;
import com.example.acabou_mony_auditing.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuditingService {

    private final AuditLogRepository repository;
    private final AuditingMapper mapper;

    @Transactional
    public AuditLogResponseDTO registrarLog(AuditLogCreateDTO dto) {
        AuditLog entity = mapper.toEntity(dto);
        AuditLog savedEntity = repository.save(entity);
        return mapper.toResponseDTO(savedEntity);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponseDTO> listarTodos(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public AuditLogResponseDTO buscarPorId(Long id) {
        AuditLog entity = repository.findById(id)
                .orElseThrow(() -> new AuditingNaoEncontradoException(
                        "Log de auditoria com ID " + id + " não foi encontrado no sistema."));
        return mapper.toResponseDTO(entity);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponseDTO> listarPorUsuario(Long usuarioId, Pageable pageable) {
        return repository.findByUsuarioId(usuarioId, pageable)
                .map(mapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponseDTO> listarPorAcao(String acao, Pageable pageable) {
        return repository.findByAcao(acao, pageable)
                .map(mapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public Page<AuditLogResponseDTO> listarPorEntidade(String entidadeNome, Long entidadeId, Pageable pageable) {
        return repository.findByEntidadeNomeAndEntidadeId(entidadeNome, entidadeId, pageable)
                .map(mapper::toResponseDTO);
    }
}