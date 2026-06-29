package com.example.acabou_mony_account.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Nome da Exchange (ponto de entrada da mensagem)
    public static final String FANOUT_EXCHANGE = "am.usuario-criado.exchange";

    // Nomes das Filas que vão receber uma cópia da mensagem
    public static final String QUEUE_NOTIFICACAO = "am.notificacao.usuario-criado";
    public static final String QUEUE_AUDITORIA = "am.auditing.usuario-criado";

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(FANOUT_EXCHANGE);
    }

    @Bean
    public Queue queueNotificacao() {
        return new Queue(QUEUE_NOTIFICACAO, true); // durable: true
    }

    @Bean
    public Queue queueAuditoria() {
        return new Queue(QUEUE_AUDITORIA, true);
    }

    // Vincula a fila de notificação à Exchange
    @Bean
    public Binding bindingNotificacao(Queue queueNotificacao, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queueNotificacao).to(fanoutExchange);
    }

    // Vincula a fila de auditoria à Exchange
    @Bean
    public Binding bindingAuditoria(Queue queueAuditoria, FanoutExchange fanoutExchange) {
        return BindingBuilder.bind(queueAuditoria).to(fanoutExchange);
    }

    // Conversor necessário para enviar o objeto Java como JSON para a fila
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}