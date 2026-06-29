package com.example.acabou_mony_auditing.repository;

import com.example.acabou_mony_auditing.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {


    Page<AuditLog> findByUsuarioId(Long usuarioId, Pageable pageable);


    Page<AuditLog> findByAcao(String acao, Pageable pageable);


    Page<AuditLog> findByEntidadeNomeAndEntidadeId(String name, Long id, Pageable pageable);
}