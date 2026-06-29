package com.example.acabou_mony_transaction.service;

import com.example.acabou_mony_transaction.client.AccountClient;
import com.example.acabou_mony_transaction.client.AuditingClient;
import com.example.acabou_mony_transaction.dto.auditoria.AuditLogDto;
import com.example.acabou_mony_transaction.dto.conta.ContaEspelhoDto;
import com.example.acabou_mony_transaction.dto.transacao.TransacaoResquestDto;
import com.example.acabou_mony_transaction.dto.transacao.TransacaoResponseDto;
import com.example.acabou_mony_transaction.entity.Transacao;
import com.example.acabou_mony_transaction.exception.TransacaoNaoEncontradaException;
import com.example.acabou_mony_transaction.mapper.TransacaoMapper;
import com.example.acabou_mony_transaction.repository.TransacaoRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final TransacaoMapper transacaoMapper;
    private final AccountClient accountClient;
    private final AuditingClient auditingClient;
    private final RabbitTemplate rabbitTemplate;
    private final MeterRegistry meterRegistry;
    private final TransactionTemplate transactionTemplate;

    // Custom metrics
    private final AtomicInteger transacaoCounter;
    private final AtomicInteger transacaoSuccessCounter;
    private final AtomicInteger transacaoFailureCounter;

    public TransacaoService(TransacaoRepository transacaoRepository,
                            TransacaoMapper transacaoMapper,
                            AccountClient accountClient,
                            AuditingClient auditingClient,
                            RabbitTemplate rabbitTemplate,
                            MeterRegistry meterRegistry,
                            TransactionTemplate transactionTemplate) {
        this.transacaoRepository = transacaoRepository;
        this.transacaoMapper = transacaoMapper;
        this.accountClient = accountClient;
        this.auditingClient = auditingClient;
        this.rabbitTemplate = rabbitTemplate;
        this.meterRegistry = meterRegistry;
        this.transactionTemplate = transactionTemplate;
        // Initialize custom metrics
        this.transacaoCounter = meterRegistry.gauge("transacao.total", new AtomicInteger(0));
        this.transacaoSuccessCounter = meterRegistry.gauge("transacao.success", new AtomicInteger(0));
        this.transacaoFailureCounter = meterRegistry.gauge("transacao.failure", new AtomicInteger(0));
    }

    /**
     * Processa uma transação financeira
     * 1. Valida o saldo da conta de origem
     * 2. Registra a transação no banco de dados
     * 3. Atualiza os saldos das contas
     * 4. Envia mensagem para fila de notificações
     * 5. Registra auditoria da transação
     *
     * @param dto os dados da transação
     * @return a transação processada
     */
    public TransacaoResponseDto processarTransacao(TransacaoResquestDto dto) {
        Timer.Sample sample = Timer.start(meterRegistry);
        log.info("Iniciando processamento de transação: origem={}, destino={}, valor={}",
                dto.getContaOrigemId(), dto.getContaDestinoId(), dto.getValor());

        transacaoCounter.incrementAndGet();

        // Passo 1: Verificar saldo (Fora de transação de banco - Chamada HTTP limpa)
        ContaEspelhoDto contaOrigem = accountClient.getBalance(dto.getContaOrigemId());
        if (contaOrigem == null) {
            log.error("Conta de origem não encontrada: {}", dto.getContaOrigemId());
            throw new IllegalArgumentException("Conta de origem não encontrada");
        }

        if (contaOrigem.getSaldo().compareTo(dto.getValor()) < 0) {
            log.warn("Saldo insuficiente na conta {}: saldo={}, valor={}",
                    dto.getContaOrigemId(), contaOrigem.getSaldo(), dto.getValor());
            throw new IllegalArgumentException("Saldo insuficiente para realizar a transação");
        }

        // Passo 2: Registrar a transação como PENDENTE e COMMITAR imediatamente no banco
        // Ao sair deste bloco, o MySQL libera QUALQUER lock que tenha sido criado!
        Transacao transacaoSalva = transactionTemplate.execute(status -> {
            Transacao transacao = transacaoMapper.toEntity(dto);
            transacao.setStatus(Transacao.StatusTransacao.PENDENTE); // Boa prática iniciar pendente
            return transacaoRepository.save(transacao);
        });

        log.info("Transação registrada temporariamente com ID: {}", transacaoSalva.getId());

        try {
            // Passo 3: Atualizar saldos das contas (Chamadas HTTP livres de locks de banco!)
            atualizarSaldos(dto, contaOrigem);

            // Passo 4: Marcar transação como concluída abrindo uma microtransação rápida
            transactionTemplate.executeWithoutResult(status -> {
                transacaoSalva.setStatus(Transacao.StatusTransacao.CONCLUIDA);
                transacaoRepository.save(transacaoSalva);
            });
            log.info("Transação concluída com sucesso: ID={}", transacaoSalva.getId());

            // Passo 5 e 6: Notificações e Auditorias (Fora de transações)
            enviarNotificacao(transacaoSalva);
            registrarAuditoria(transacaoSalva, "TRANSACAO_CONCLUIDA");

            transacaoSuccessCounter.incrementAndGet();
            // (seu código de métricas sample.stop...)

            return transacaoMapper.toResponseDto(transacaoSalva);

        } catch (Exception e) {
            log.error("Erro ao processar transação: {}", e.getMessage(), e);

            // Em caso de falha, abre outra microtransação rápida para atualizar o status para FALHA
            transactionTemplate.executeWithoutResult(status -> {
                transacaoSalva.setStatus(Transacao.StatusTransacao.FALHA);
                transacaoRepository.save(transacaoSalva);
            });

            registrarAuditoria(transacaoSalva, "TRANSACAO_FALHA");
            transacaoFailureCounter.incrementAndGet();
            // (seu código de métricas sample.stop...)

            throw new RuntimeException("Erro ao processar transação: " + e.getMessage(), e);
        }
    }

    /**
     * Atualiza os saldos das contas de origem e destino
     *
     * @param dto os dados da transação
     * @param contaOrigem os dados da conta de origem
     */
    private void atualizarSaldos(TransacaoResquestDto dto, ContaEspelhoDto contaOrigem) {
        // Debitar da conta de origem
        BigDecimal novoSaldoOrigem = contaOrigem.getSaldo().subtract(dto.getValor());
        contaOrigem.setSaldo(novoSaldoOrigem);
        accountClient.updateBalance(dto.getContaOrigemId(), contaOrigem);
        log.info("Saldo debitado da conta {}: novo saldo={}", dto.getContaOrigemId(), novoSaldoOrigem);

        // Creditar na conta de destino
        ContaEspelhoDto contaDestino = accountClient.getBalance(dto.getContaDestinoId());
        if (contaDestino == null) {
            throw new IllegalArgumentException("Conta de destino não encontrada");
        }
        BigDecimal novoSaldoDestino = contaDestino.getSaldo().add(dto.getValor());
        contaDestino.setSaldo(novoSaldoDestino);
        accountClient.updateBalance(dto.getContaDestinoId(), contaDestino);
        log.info("Saldo creditado na conta {}: novo saldo={}", dto.getContaDestinoId(), novoSaldoDestino);
    }

    /**
     * Envia uma mensagem de notificação para a fila RabbitMQ
     *
     * @param transacao a transação processada
     */
    private void enviarNotificacao(Transacao transacao) {
        try {
            TransacaoResponseDto dto = transacaoMapper.toResponseDto(transacao);
            rabbitTemplate.convertAndSend("transacao.exchange", "transacao.concluida", dto);
            log.info("Notificação enviada para fila RabbitMQ: transacao ID={}", transacao.getId());
        } catch (Exception e) {
            log.error("Erro ao enviar notificação para RabbitMQ: {}", e.getMessage(), e);
        }
    }

    /**
     * Registra uma entrada de auditoria para a transação
     * Falhas não bloqueiam o processamento (fire-and-forget)
     *
     * @param transacao a transação processada
     * @param operacao a operação realizada
     */
    private void registrarAuditoria(Transacao transacao, String operacao) {
        try {
            AuditLogDto auditLog = AuditLogDto.builder()
                    .transacaoId(transacao.getId())
                    .contaOrigemId(transacao.getContaOrigemId())
                    .contaDestinoId(transacao.getContaDestinoId())
                    .valor(transacao.getValor())
                    .status(transacao.getStatus().toString())
                    .timestamp(LocalDateTime.now())
                    .operacao(operacao)
                    .descricao("Transação de " + transacao.getTipo() + " no valor de " + transacao.getValor())
                    .build();

            auditingClient.logTransaction(auditLog);
        } catch (Exception e) {
            log.error("Erro ao registrar auditoria da transação: {}", e.getMessage(), e);
            // Do NOT rethrow - auditing failures should not block transaction processing
        }
    }
    /**
     * Recupera uma transação pelo ID
     *
     * @param id o ID da transação
     * @return a transação encontrada
     */
    @Transactional(readOnly = true)
    public TransacaoResponseDto obterTransacao(Long id) {
        log.info("Buscando transação com ID: {}", id);
        Transacao transacao = transacaoRepository.findById(id)
                .orElseThrow(() -> new TransacaoNaoEncontradaException("Transação não encontrada com ID: " + id));
        return transacaoMapper.toResponseDto(transacao);
    }

    /**
     * Recupera todas as transações de uma conta
     *
     * @param contaId o ID da conta
     * @return lista de transações
     */
    @Transactional(readOnly = true)
    public List<TransacaoResponseDto> obterTransacoesPorConta(Long contaId) {
        log.info("Buscando transações para conta: {}", contaId);
        List<Transacao> transacoes = transacaoRepository.findByContaOrigemIdOrContaDestinoId(contaId, contaId);
        return transacoes.stream()
                .map(transacaoMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    /**
     * Recupera todas as transações
     *
     * @return lista de todas as transações
     */
    @Transactional(readOnly = true)
    public List<TransacaoResponseDto> obterTodasAsTransacoes() {
        log.info("Buscando todas as transações");
        List<Transacao> transacoes = transacaoRepository.findAll();
        return transacoes.stream()
                .map(transacaoMapper::toResponseDto)
                .collect(Collectors.toList());
    }
}

