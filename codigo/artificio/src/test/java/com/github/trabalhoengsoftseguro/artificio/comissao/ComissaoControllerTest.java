package com.github.trabalhoengsoftseguro.artificio.comissao;

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
class ComissaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Deve barrar visitante anonimo tentando acessar encomendas de cliente")
    void deveBarrarAnonimoNoPainelCliente() throws Exception {
        mockMvc.perform(get("/painel/cliente/pedidos"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(username = "lucas@artificio.local", roles = {"CLIENTE"})
    @DisplayName("Deve permitir cliente autenticado acessar suas encomendas")
    void devePermitirClienteAcessarEncomendas() throws Exception {
        mockMvc.perform(get("/painel/cliente/pedidos"))
                .andExpect(status().isOk())
                .andExpect(view().name("cliente/pedidos"))
                .andExpect(model().attributeExists("pedidos"))
                .andExpect(content().string(containsString("Minhas Encomendas de Arte")));
    }

    @Test
    @WithMockUser(username = "lucas@artificio.local", roles = {"CLIENTE"})
    @DisplayName("Deve carregar tela de solicitacao de comissao para modalidade existente")
    void deveCarregarFormularioDeSolicitacao() throws Exception {
        mockMvc.perform(get("/comissoes/solicitar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("cliente/solicitar"))
                .andExpect(model().attributeExists("tipo"))
                .andExpect(model().attributeExists("solicitacaoDto"))
                .andExpect(content().string(containsString("Briefing e Instruções")));
    }
}
