package com.example.acabou_mony_account.dto.usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.hibernate.validator.constraints.br.CPF;

@Data
public class UsuarioRequestDto {

    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Por favor, insira um e-mail válido.")
    private String email;

    @NotBlank(message = "O CPF é obrigatório.")
    @CPF(message = "O CPF inserido é inválido.") // Validação oficial de formato de CPF brasileiro
    private String cpf;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    private String senha;
}