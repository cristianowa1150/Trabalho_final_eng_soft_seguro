package com.github.trabalhoengsoftseguro.artificio.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Fornece atributos globais compartilhados por todas as views Thymeleaf.
 * Controla a visibilidade dos facilitadores de teste/demonstração com base no ambiente.
 */
@ControllerAdvice
public class GlobalModelAdvice {

    private final boolean exibirCredenciaisDemo;

    public GlobalModelAdvice(
            Environment environment,
            @Value("${artificio.demo.exibir-credenciais:true}") boolean exibirCredenciaisConfig) {
        boolean isProd = environment.acceptsProfiles(Profiles.of("prod"));
        this.exibirCredenciaisDemo = !isProd && exibirCredenciaisConfig;
    }

    @ModelAttribute("exibirCredenciaisDemo")
    public boolean getExibirCredenciaisDemo() {
        return exibirCredenciaisDemo;
    }
}
