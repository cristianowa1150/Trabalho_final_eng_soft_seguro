package com.github.trabalhoengsoftseguro.artificio.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

/**
 * Implementação simulada para ambiente de desenvolvimento e testes (!prod).
 * Imprime os dados do e-mail e o link de ativação diretamente no console do terminal
 * (output de ./mvnw spring-boot:run) para fácil acesso e teste manual.
 */
@Service
@Profile("!prod")
public class SimulatedEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(SimulatedEmailService.class);

    @Override
    public void enviarEmailConfirmacao(String destinatario, String nomeUsuario, String linkAtivacao) {
        String separador = "=================================================================================";
        String banner = """
                
                %s
                📧 [SIMULADOR DE E-MAIL - ARTIFÍCIO]
                Para: %s <%s>
                Assunto: Confirme seu cadastro na plataforma Artifício
                
                Olá, %s! Para confirmar a titularidade do seu e-mail e ativar sua conta,
                clique ou acesse o link abaixo:
                👉 Link de Ativação: %s
                %s
                """.formatted(separador, nomeUsuario, destinatario, nomeUsuario, linkAtivacao, separador);

        // Imprime diretamente na saída padrão para visibilidade imediata no terminal de execução
        System.out.println(banner);
        log.info("E-mail simulado de confirmação gerado para {} com link: {}", destinatario, linkAtivacao);
    }
}
