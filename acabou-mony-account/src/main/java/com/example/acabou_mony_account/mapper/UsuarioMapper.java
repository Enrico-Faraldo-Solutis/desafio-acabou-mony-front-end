package com.example.acabou_mony_account.mapper;

import com.example.acabou_mony_account.dto.usuario.UsuarioRequestDto;
import com.example.acabou_mony_account.dto.usuario.UsuarioResponseDto;
import com.example.acabou_mony_account.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toEntity(UsuarioRequestDto dto) {
        if (dto == null) {
            return null;
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setCpf(dto.getCpf());
        usuario.setSenhaHash(dto.getSenha());

        return usuario;
    }

    public UsuarioResponseDto toDto(Usuario entity) {
        if (entity == null) {
            return null;
        }

        UsuarioResponseDto dto = new UsuarioResponseDto();
        dto.setId(entity.getId());
        dto.setNome(entity.getNome());
        dto.setEmail(entity.getEmail());
        dto.setCpf(entity.getCpf());
        dto.setDataCriacao(entity.getDataCriacao());

        return dto;
    }
}