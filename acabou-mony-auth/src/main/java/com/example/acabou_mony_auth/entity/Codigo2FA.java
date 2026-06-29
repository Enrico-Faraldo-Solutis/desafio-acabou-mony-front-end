package com.example.acabou_mony_auth.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "codigos_2fa")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Codigo2FA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(nullable = false, length = 6)
    private String codigo;

    @Column(name = "data_expiracao", nullable = false)
    private LocalDateTime expiraEm;

    @Column(name = "usado", nullable = false)
    private boolean utilizado;

}