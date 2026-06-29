package com.example.notificacao_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String FILA_USUARIO_CRIADO = "am.notificacao.usuario-criado";
    public static final String FILA_TRANSACAO_CONCLUIDA = "am.notificacao.transacao-concluida";


    public static final String EXCHANGE_TRANSACAO = "transacao.exchange";
    public static final String ROTA_TRANSACAO = "transacao.concluida";

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Queue queueUsuarioCriado() {
        return new Queue(FILA_USUARIO_CRIADO, true); // true = durável
    }

    @Bean
    public Queue queueTransacaoConcluida() {
        return new Queue(FILA_TRANSACAO_CONCLUIDA, true);
    }


    @Bean
    public DirectExchange transacaoExchange() {
        return new DirectExchange(EXCHANGE_TRANSACAO);
    }


    @Bean
    public Binding bindingTransacaoConcluida(Queue queueTransacaoConcluida, DirectExchange transacaoExchange) {
        return BindingBuilder.bind(queueTransacaoConcluida)
                .to(transacaoExchange)
                .with(ROTA_TRANSACAO);
    }
}