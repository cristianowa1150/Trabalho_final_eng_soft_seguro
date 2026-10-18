package com.github.trabalhoengsoftseguro.artificio.artista;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PainelArtistaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Deve barrar usuario anonimo tentando acessar o painel do artista")
    void deveBarrarUsuarioAnonimo() throws Exception {
        mockMvc.perform(get("/painel/artista/pedidos"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(username = "lucas@artificio.local", roles = {"CLIENTE"})
    @DisplayName("Deve negar acesso (403) para cliente comum tentando acessar painel do artista")
    void deveNegarAcessoParaClienteComum() throws Exception {
        mockMvc.perform(get("/painel/artista/pedidos"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "carlos@artificio.local", roles = {"ARTISTA"})
    @DisplayName("Deve autorizar artista autenticado a visualizar seus pedidos recebidos")
    void deveAutorizarArtistaAcessarPedidos() throws Exception {
        mockMvc.perform(get("/painel/artista/pedidos"))
                .andExpect(status().isOk())
                .andExpect(view().name("artista/pedidos"))
                .andExpect(model().attributeExists("pedidos"))
                .andExpect(content().string(containsString("Painel de Pedidos Recebidos")));
    }

    @Test
    @WithMockUser(username = "carlos@artificio.local", roles = {"ARTISTA"})
    @DisplayName("Deve renderizar a tela de gerenciamento de portfolio do artista logado")
    void deveRenderizarPortfolioDoArtista() throws Exception {
        mockMvc.perform(get("/painel/artista/portfolio"))
                .andExpect(status().isOk())
                .andExpect(view().name("artista/portfolio"))
                .andExpect(model().attributeExists("obras"))
                .andExpect(model().attributeExists("novaObraDto"));
    }
}
