package com.github.trabalhoengsoftseguro.artificio.email;

/**
 * Contrato de serviço para envio de e-mails da plataforma Artifício.
 */
public interface EmailService {

    /**
     * Envia o e-mail com link de confirmação e ativação da conta recém-cadastrada.
     *
     * @param destinatario Endereço de e-mail do usuário
     * @param nomeUsuario  Nome do usuário
     * @param linkAtivacao URL completa ou relativa contendo o token de ativação
     */
    void enviarEmailConfirmacao(String destinatario, String nomeUsuario, String linkAtivacao);
}
