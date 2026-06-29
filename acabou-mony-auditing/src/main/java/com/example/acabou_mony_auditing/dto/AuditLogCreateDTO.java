package com.example.acabou_mony_auditing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogCreateDTO {

    private Long usuarioId;

    @NotBlank(message = "A ação não pode estar em branco.")
    @Size(max = 100, message = "A ação deve ter no máximo 100 caracteres.")
    private String acao;

    @NotBlank(message = "O nome da entidade não pode estar em branco.")
    @Size(max = 100, message = "O nome da entidade deve ter no máximo 100 caracteres.")
    private String entidadeNome;

    @NotNull(message = "O ID da entidade mapeada é obrigatório.")
    private Long entidadeId;

    @NotNull(message = "O campo de detalhes, mesmo que vazio, deve ser enviado.")
    private Map<String, Object> detalhes;

    @Size(max = 45, message = "O IP de origem deve ter no máximo 45 caracteres.")
    private String ipOrigem;
}