package com.example.acabou_mony_transaction.repository;

import com.example.acabou_mony_transaction.entity.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    /**
     * Encontra todas as transações de uma conta específica
     *
     * @param contaId o ID da conta
     * @return lista de transações
     */
    List<Transacao> findByContaOrigemIdOrContaDestinoId(Long contaOrigemId, Long contaDestinoId);
}
