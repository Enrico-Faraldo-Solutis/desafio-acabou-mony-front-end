package com.example.acabou_mony_account.service;

import com.example.acabou_mony_account.dto.conta.ContaResponseDto;
import com.example.acabou_mony_account.dto.saldo.AtualizarSaldoDto;
import com.example.acabou_mony_account.entity.Conta;
import com.example.acabou_mony_account.exception.ContaNaoEncontradaException;
import com.example.acabou_mony_account.mapper.ContaMapper;
import com.example.acabou_mony_account.repository.ContaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContaService {

    private final ContaRepository contaRepository;
    private final ContaMapper contaMapper;

    public ContaResponseDto consultarConta(Long contaId) {
        Conta conta = contaRepository.findById(contaId)
                .orElseThrow(() -> new ContaNaoEncontradaException());

        return contaMapper.toDto(conta);
    }

    @Transactional
    public ContaResponseDto atualizarSaldo(AtualizarSaldoDto dto) {
        Conta conta = contaRepository.findById(dto.getContaId())
                .orElseThrow(() -> new ContaNaoEncontradaException());

        if (dto.getValor().compareTo(BigDecimal.ZERO) < 0) {
            BigDecimal saldoAposOperacao = conta.getSaldo().add(dto.getValor());

            if (saldoAposOperacao.compareTo(BigDecimal.ZERO) < 0) {
                throw new RuntimeException("Saldo insuficiente para realizar a operação.");
            }
        }

        conta.setSaldo(conta.getSaldo().add(dto.getValor()));

        Conta contaAtualizada = contaRepository.save(conta);

        return contaMapper.toDto(contaAtualizada);
    }

        public Page<ContaResponseDto> listarTodasContas(Pageable pageable) {
        return contaRepository.findAll(pageable)
                .map(contaMapper::toDto);
    }

    public Page<ContaResponseDto> listarContasPorUsuario(Long usuarioId, Pageable pageable) {
        return contaRepository.findByUsuarioId(usuarioId, pageable)
                .map(contaMapper::toDto);
    }
}