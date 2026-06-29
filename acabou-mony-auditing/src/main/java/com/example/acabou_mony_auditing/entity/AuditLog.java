package com.example.acabou_mony_auditing.entity;

import com.example.acabou_mony_auditing.util.JsonToMapConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "audit_logs")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(nullable = false, length = 100)
    private String acao;

    @Column(name = "entidade_nome", nullable = false, length = 100)
    private String entidadeNome;

    @Column(name = "entidade_id", nullable = false)
    private Long entidadeId;

    /* Aqui acontece a mágica: O JPA vai interceptar este Map
       e usar o nosso converter para salvar como JSON no MySQL
    */
    @Column(columnDefinition = "json")
    @Convert(converter = JsonToMapConverter.class)
    private Map<String, Object> detalhes;

    @Column(name = "ip_origem", length = 45)
    private String ipOrigem;

    @CreationTimestamp
    @Column(name = "timestamp", nullable = false, updatable = false)
    private LocalDateTime timestamp;
}