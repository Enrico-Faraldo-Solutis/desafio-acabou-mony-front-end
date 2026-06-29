package com.example.notificacao_service.repository;

import com.example.notificacao_service.entity.Notificacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacaoRepository extends JpaRepository<Notificacao, Long> {

    Page<Notificacao> findByUsuarioId(Long usuarioId, Pageable pageable);

    Page<Notificacao> findByUsuarioIdAndLida(Long usuarioId, boolean lida, Pageable pageable);
}