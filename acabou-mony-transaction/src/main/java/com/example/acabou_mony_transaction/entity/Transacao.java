package com.example.acabou_mony_transaction.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conta_origem_id", nullable = false)
    private Long contaOrigemId;

    @Column(name = "conta_destino_id", nullable = false)
    private Long contaDestinoId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTransacao status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipo;

    @Column(name = "data_transacao", nullable = false, updatable = false)
    private LocalDateTime dataTransacao;

    @PrePersist
    protected void onCreate() {
        if (this.dataTransacao == null) {
            this.dataTransacao = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = StatusTransacao.PENDENTE;
        }
    }

    public enum StatusTransacao {
        PENDENTE,
        CONCLUIDA,
        FALHA
    }

    public enum TipoTransacao {
        DEBITO,
        CREDITO,
        TRANSFERENCIA
    }
}
