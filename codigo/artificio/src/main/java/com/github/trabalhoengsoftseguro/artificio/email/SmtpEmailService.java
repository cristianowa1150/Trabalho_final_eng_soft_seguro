package com.github.trabalhoengsoftseguro.artificio.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

/**
 * Implementação de envio de e-mails para ambiente de produção (prod).
 * Conecta a um servidor SMTP / submission configurado via properties.
 */
@Service
@Profile("prod")
public class SmtpEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(SmtpEmailService.class);

    @Value("${artificio.mail.smtp.host:localhost}")
    private String smtpHost;

    @Value("${artificio.mail.smtp.port:587}")
    private int smtpPort;

    @Value("${artificio.mail.from:noreply@artificio.local}")
    private String remetente;

    @Override
    public void enviarEmailConfirmacao(String destinatario, String nomeUsuario, String linkAtivacao) {
        log.info("Enviando e-mail de ativação via SMTP {}:{} para {}", smtpHost, smtpPort, destinatario);
        try (Socket socket = new Socket(smtpHost, smtpPort);
             PrintWriter writer = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8)) {
            writer.println("HELO artificio.local");
            writer.println("MAIL FROM:<" + remetente + ">");
            writer.println("RCPT TO:<" + destinatario + ">");
            writer.println("DATA");
            writer.println("Subject: Ative sua conta no Artificio");
            writer.println("From: " + remetente);
            writer.println("To: " + destinatario);
            writer.println();
            writer.println("Ola " + nomeUsuario + ",");
            writer.println("Para ativar sua conta na plataforma Artificio, acesse o link abaixo:");
            writer.println(linkAtivacao);
            writer.println(".");
            writer.println("QUIT");
            log.info("E-mail de ativação enviado com sucesso para {}", destinatario);
        } catch (Exception ex) {
            log.error("Erro ao enviar e-mail via servidor SMTP {}:{}. Detalhes: {}", smtpHost, smtpPort, ex.getMessage());
        }
    }
}
