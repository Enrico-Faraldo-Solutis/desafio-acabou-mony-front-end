package com.example.notificacao_service.consumer;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.amqp.rabbit.annotation.Queue; // 🌟 Importante para o truque da fila automática
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class ClienteConsumer {

    @Value("${sendgrid.api-key}")
    private String apiKey;

    @Value("${sendgrid.from-email}")
    private String fromEmail;

    // 1. Ouvinte da fila de Transações (Apenas Log no Console)
    @RabbitListener(queues = "am.notificacao.transacao-concluida")
    public void consumirTransacao(Map<String, Object> dados) {
        String idTransacao = String.valueOf(dados.get("id"));
        String valor = String.valueOf(dados.get("valor"));
        String contaOrigem = String.valueOf(dados.get("contaOrigemId"));
        String contaDestino = String.valueOf(dados.get("contaDestinoId"));

        System.out.println("=================================================");
        System.out.println("💸 NOTIFICAÇÃO DE TRANSAÇÃO RECEBIDA!");
        System.out.println("ID: " + idTransacao);
        System.out.println("De Conta: " + contaOrigem + " -> Para Conta: " + contaDestino);
        System.out.println("Valor: R$ " + valor);
        System.out.println("=================================================");
    }

    // 2. Ouvinte da fila de Boas-vindas (Envio de E-mail Real)
    @RabbitListener(queues = "am.notificacao.usuario-criado")
    public void consumir(Map<String, Object> dados) {
        String emailDestinatario = (String) dados.get("email");
        String nomeDestinatario = (String) dados.get("nome");
        String idUsuario = String.valueOf(dados.get("id"));

        System.out.println("=================================================");
        System.out.println("Notificação recebida. Enviando e-mail da Fintech para: " + emailDestinatario);
        System.out.println("=================================================");

        enviarEmailFintech(emailDestinatario, nomeDestinatario, idUsuario);
    }

    // 3. 🌟 NOVO: Ouvinte da fila de 2FA (Cria a fila automaticamente se não existir)
    @RabbitListener(queuesToDeclare = @Queue("am.notificacao.2fa-solicitado"))
    public void consumir2FA(Map<String, Object> dados) {
        String emailDestinatario = (String) dados.get("email");
        String nomeDestinatario = (String) dados.get("nome");
        String codigo2FA = String.valueOf(dados.get("codigo"));

        System.out.println("=================================================");
        System.out.println("🔐 SOLICITAÇÃO DE 2FA RECEBIDA!");
        System.out.println("Enviando código para: " + emailDestinatario);
        System.out.println("Código Gerado: " + codigo2FA);
        System.out.println("=================================================");

        enviarEmail2FA(emailDestinatario, nomeDestinatario, codigo2FA);
    }

    // --- MÉTODOS PRIVADOS DE ENVIO VIA SENDGRID ---

    private void enviarEmailFintech(String para, String nome, String idUsuario) {
        Email from = new Email(fromEmail);
        String subject = "Sua conta no Acabou o Mony foi aberta! 🎉";
        Email to = new Email(para);

        String textoMensagem = String.format(
                "Olá %s,\n\n" +
                        "Seja muito bem-vindo à Fintech Acabou o Mony!\n\n" +
                        "Sua conta digital foi criada com sucesso (ID: %s) e já está pronta para receber depósitos e transações.\n\n" +
                        "Bons investimentos,\nEquipe Acabou o Mony.",
                nome, idUsuario
        );

        dispararNoSendGrid(from, subject, to, textoMensagem, "E-mail de boas-vindas");
    }

    private void enviarEmail2FA(String para, String nome, String codigo) {
        Email from = new Email(fromEmail);
        String subject = codigo + " é o seu código de verificação do Acabou o Mony 🛡️";
        Email to = new Email(para);

        String textoMensagem = String.format(
                "Olá %s,\n\n" +
                        "Você solicitou um acesso ao Acabou o Mony. Use o código abaixo para concluir seu login:\n\n" +
                        "👉 %s \n\n" +
                        "Este código expira em 5 minutos. Se não foi você quem solicitou, ignore este e-mail.\n\n" +
                        "Equipe de Segurança Acabou o Mony.",
                nome, codigo
        );

        dispararNoSendGrid(from, subject, to, textoMensagem, "E-mail de 2FA");
    }

    // Método genérico para reaproveitar a chamada do SendGrid e limpar o código
    private void dispararNoSendGrid(Email from, String subject, Email to, String texto, String tipoEmail) {
        Content content = new Content("text/plain", texto);
        Mail mail = new Mail(from, subject, to, content);
        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            Response response = sg.api(request);

            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                System.out.println("[SendGrid] " + tipoEmail + " enviado com sucesso para " + to.getEmail());
            } else {
                System.err.println("[SendGrid] Erro ao enviar " + tipoEmail + ": " + response.getBody());
            }
        } catch (IOException ex) {
            System.err.println("[SendGrid] Erro de rede/comunicação no " + tipoEmail + ": " + ex.getMessage());
        }
    }
}