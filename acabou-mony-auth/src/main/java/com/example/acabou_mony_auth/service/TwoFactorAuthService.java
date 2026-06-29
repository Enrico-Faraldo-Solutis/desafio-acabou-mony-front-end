package com.example.acabou_mony_auth.service;

import com.example.acabou_mony_auth.entity.Codigo2FA;
import com.example.acabou_mony_auth.exception.AuthException;
import com.example.acabou_mony_auth.repository.Codigo2FARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwoFactorAuthService {

    private final Codigo2FARepository codigo2FARepository;
    private final RabbitTemplate rabbitTemplate;
    @Value("${twofa.expiration.minutes:5}")
    private int expiracaoMinutos;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public void gerarEEnviarCodigo(Long usuarioId, String email) {
        List<Codigo2FA> codigosAtivos = codigo2FARepository.findByUsuarioIdAndUtilizadoFalse(usuarioId);
        codigosAtivos.forEach(codigo -> codigo.setUtilizado(true));
        codigo2FARepository.saveAll(codigosAtivos);

        String codigo = String.format("%06d", secureRandom.nextInt(1_000_000));

        Codigo2FA codigo2FA = new Codigo2FA();
        codigo2FA.setUsuarioId(usuarioId);
        codigo2FA.setCodigo(codigo);
        codigo2FA.setExpiraEm(LocalDateTime.now().plusMinutes(expiracaoMinutos));
        codigo2FA.setUtilizado(false);

        codigo2FARepository.save(codigo2FA);

        // 🌟 2. A MÁGICA DO ENVIO AUTOMÁTICO ACONTECE AQUI:
        try {
            Map<String, Object> dados2FA = new HashMap<>();
            dados2FA.put("email", email);
            dados2FA.put("codigo", codigo);
            dados2FA.put("nome", "Cliente Acabou o Mony"); // Nome genérico já que o método não recebe o nome

            // Envia direto para a fila de 2FA usando a Exchange padrão ("")
            rabbitTemplate.convertAndSend("", "am.notificacao.2fa-solicitado", dados2FA);

            log.info(" [2FA] Evento de código enviado para o RabbitMQ com sucesso!");
        } catch (Exception e) {
            log.error(" [2FA] Erro ao enviar código para o RabbitMQ: {}", e.getMessage());
        }
    }

    @Transactional
    public void validarCodigo(Long usuarioId, String codigo) {
        Codigo2FA codigo2FA = codigo2FARepository
                .findFirstByUsuarioIdAndUtilizadoFalseOrderByIdDesc(usuarioId)
                .orElseThrow(() -> new AuthException("Nenhum código 2FA ativo encontrado. Faça login novamente."));

        if (codigo2FA.getExpiraEm().isBefore(LocalDateTime.now())) {
            throw new AuthException("Código 2FA expirado. Solicite um novo código.");
        }

        if (!codigo2FA.getCodigo().equals(codigo)) {
            throw new AuthException("Código 2FA inválido.");
        }

        codigo2FA.setUtilizado(true);
        codigo2FARepository.save(codigo2FA);
    }
}