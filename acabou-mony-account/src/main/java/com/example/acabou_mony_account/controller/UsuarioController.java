package com.example.acabou_mony_account.controller;

import com.example.acabou_mony_account.dto.usuario.UsuarioRequestDto;
import com.example.acabou_mony_account.dto.usuario.UsuarioResponseDto;
import com.example.acabou_mony_account.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts/users")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<UsuarioResponseDto> criarUsuario(@Valid @RequestBody UsuarioRequestDto request) {
        UsuarioResponseDto novoUsuario = usuarioService.criarUsuario(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(novoUsuario);
    }

    @GetMapping
    public ResponseEntity<Page<UsuarioResponseDto>> listarTodosUsuarios(
            @PageableDefault(page = 0, size = 10, sort = "id") Pageable pageable) {

        Page<UsuarioResponseDto> usuarios = usuarioService.listarTodosUsuarios(pageable);
        return ResponseEntity.ok(usuarios);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDto> buscarUsuarioPorId(@PathVariable Long id) {
        UsuarioResponseDto usuario = usuarioService.buscarUsuarioPorId(id);
        return ResponseEntity.ok(usuario);
    }
}