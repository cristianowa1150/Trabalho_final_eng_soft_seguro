package com.github.trabalhoengsoftseguro.artificio.imagem;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ImagemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ImagemService imagemService;

    @Test
    @DisplayName("Deve renderizar imagem publica com headers corretos de Content-Type, Cache-Control, CSP e nosniff")
    void deveRenderizarImagemComHeadersCorretos() throws Exception {
        byte[] dados = "<svg></svg>".getBytes(StandardCharsets.UTF_8);
        String imagemId = imagemService.salvar(dados, "arte_teste.svg", "image/svg+xml");

        mockMvc.perform(get("/imagens/" + imagemId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", containsString("image/svg+xml")))
                .andExpect(header().string("Cache-Control", containsString("max-age=86400, public")))
                .andExpect(header().string("Content-Security-Policy", containsString("sandbox")))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                .andExpect(header().string("Content-Disposition", containsString("inline")))
                .andExpect(content().bytes(dados));
    }

    @Test
    @DisplayName("Deve aplicar CSP sandbox e nosniff em imagens para isolar execucao de scripts e cookies")
    void deveAplicarCspSandboxENosniff() throws Exception {
        byte[] dados = "<svg><script>alert(1)</script></svg>".getBytes(StandardCharsets.UTF_8);
        String imagemId = imagemService.salvar(dados, "xss.svg", "image/svg+xml");

        mockMvc.perform(get("/imagens/" + imagemId))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Security-Policy", containsString("default-src 'none'")))
                .andExpect(header().string("Content-Security-Policy", containsString("sandbox")))
                .andExpect(header().string("X-Content-Type-Options", "nosniff"));
    }

    @Test
    @DisplayName("Deve retornar 404 Not Found para identificador de imagem inexistente")
    void deveRetornar404ParaImagemInexistente() throws Exception {
        mockMvc.perform(get("/imagens/uuid-inexistente-12345"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve garantir acesso anonimo publico para carregamento de imagens na vitrine")
    void devePermitirAcessoAnonimoAImagens() throws Exception {
        byte[] dados = "PNG_DEMO".getBytes(StandardCharsets.UTF_8);
        String imagemId = imagemService.salvar(dados, "preview.png", "image/png");

        // Executado sem autenticacao
        mockMvc.perform(get("/imagens/" + imagemId))
                .andExpect(status().isOk())
                .andExpect(content().bytes(dados));
    }
}
