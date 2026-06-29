package com.example.acabou_mony_account.service;

import com.example.acabou_mony_account.config.RabbitMQConfig;
import com.example.acabou_mony_account.dto.event.UsuarioCriadoEvent;
import com.example.acabou_mony_account.dto.usuario.UsuarioRequestDto;
import com.example.acabou_mony_account.dto.usuario.UsuarioResponseDto;
import com.example.acabou_mony_account.entity.Conta;
import com.example.acabou_mony_account.entity.StatusConta;
import com.example.acabou_mony_account.entity.Usuario;
import com.example.acabou_mony_account.exception.ConflitoDeDadosException;
import com.example.acabou_mony_account.mapper.UsuarioMapper;
import com.example.acabou_mony_account.repository.ContaRepository;
import com.example.acabou_mony_account.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final ContaRepository contaRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public UsuarioResponseDto criarUsuario(UsuarioRequestDto request) {

        Usuario usuario = usuarioMapper.toEntity(request);
        String senhaCriptografada = passwordEncoder.encode(request.getSenha());
        usuario.setSenhaHash(senhaCriptografada);
        usuario.setDataCriacao(LocalDateTime.now());

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        Conta novaConta = new Conta();
        novaConta.setUsuario(usuarioSalvo);
        novaConta.setSaldo(BigDecimal.ZERO);
        novaConta.setStatus(StatusConta.ATIVA);
        novaConta.setDataCriacao(LocalDateTime.now());
        contaRepository.save(novaConta);

        try {
            UsuarioCriadoEvent event = new UsuarioCriadoEvent(
                    usuarioSalvo.getId(),
                    usuarioSalvo.getNome(),
                    usuarioSalvo.getEmail(),
                    usuarioSalvo.getCpf()
            );

            rabbitTemplate.convertAndSend(RabbitMQConfig.FANOUT_EXCHANGE, "", event);
            System.out.println("Evento de usuário criado enviado para o RabbitMQ com sucesso!");
        } catch (Exception e) {
            System.err.println("Erro ao enviar evento para o RabbitMQ: " + e.getMessage());
        }

        return usuarioMapper.toDto(usuarioSalvo);
    }

    public UsuarioResponseDto buscarUsuarioPorId(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + id));
        return usuarioMapper.toDto(usuario);
    }

    public Page<UsuarioResponseDto> listarTodosUsuarios(Pageable pageable) {
        return usuarioRepository.findAll(pageable)
                .map(usuarioMapper::toDto); // O .map() do Page já faz a conversão perfeitamente!
    }
}