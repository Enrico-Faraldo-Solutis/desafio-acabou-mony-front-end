package com.example.acabou_mony_card.service;

import com.example.acabou_mony_card.client.AccountClient;
import com.example.acabou_mony_card.dto.CardGenerationRequestDTO;
import com.example.acabou_mony_card.dto.CardResponseDTO;
import com.example.acabou_mony_card.entity.Cartao;
import com.example.acabou_mony_card.exception.CartaoNaoEncontradoException;
import com.example.acabou_mony_card.mapper.CartaoMapper;
import com.example.acabou_mony_card.repository.CartaoRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CartaoRepository cartaoRepository;
    private final CartaoMapper cartaoMapper;
    private final AccountClient accountClient;
    private final Random random = new Random();

    @Transactional
    public CardResponseDTO gerarCartao(CardGenerationRequestDTO request) {

        // Deixe apenas a chamada direta. Se a conta não existir,
        // o Feign vai lançar o NotFound e o seu Handler vai capturar!
        accountClient.verificarContaExistente(request.getContaId());

        Cartao cartao = cartaoMapper.toEntity(request);

        cartao.setNumeroCartao(gerarNumeroCartaoFicticio());
        cartao.setCvv(String.format("%03d", random.nextInt(1000)));
        cartao.setDataValidade(LocalDate.now().plusYears(5));
        cartao.setAtivo(true);

        Cartao cartaoSalvo = cartaoRepository.save(cartao);
        return cartaoMapper.toDto(cartaoSalvo);
    }

    @Transactional(readOnly = true)
    public List<CardResponseDTO> listarCartoesPorConta(Long contaId) {
        return cartaoRepository.findByContaId(contaId)
                .stream()
                .map(cartaoMapper::toDto)
                .toList();
    }

    @Transactional
    public CardResponseDTO alternarStatusCartao(Long id) {
        Cartao cartao = cartaoRepository.findById(id)
                .orElseThrow(() -> new CartaoNaoEncontradoException("Cartão não localizado com o ID: " + id));

        // Inverte o estado atual do cartão (Ativa / Desativa)
        cartao.setAtivo(!cartao.isAtivo());
        return cartaoMapper.toDto(cartaoRepository.save(cartao));
    }

    private String gerarNumeroCartaoFicticio() {
        StringBuilder sb = new StringBuilder("4"); // Começa com 4 para simular bandeira Visa
        for (int i = 0; i < 15; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}