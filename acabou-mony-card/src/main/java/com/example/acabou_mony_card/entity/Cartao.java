package com.example.acabou_mony_card.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "cartoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Cartao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "conta_id", nullable = false) // Mapeia para conta_id no banco
    private Long contaId;

    @Column(name = "numero_criptografado", nullable = false, unique = true, length = 16)
    private String numeroCartao;

    @Column(name = "nome_impresso", nullable = false)
    private String nomeImpresso;

    @Column(name = "cvc_criptografado", nullable = false, length = 3)
    private String cvv;

    @Column(name = "data_validade", nullable = false)
    private LocalDate dataValidade;

    @Column(nullable = false)
    private boolean ativo;
}