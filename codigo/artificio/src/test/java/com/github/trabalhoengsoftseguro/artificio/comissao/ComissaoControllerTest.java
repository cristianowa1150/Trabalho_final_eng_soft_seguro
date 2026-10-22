package com.github.trabalhoengsoftseguro.artificio.comissao;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
    @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
    @DisplayName("Deve permitir cliente autenticado acessar suas encomendas")
    void devePermitirClienteAcessarEncomendas() throws Exception {
        mockMvc.perform(get("/painel/cliente/pedidos"))
                .andExpect(status().isOk())
                .andExpect(view().name("cliente/pedidos"))
                .andExpect(model().attributeExists("pedidos"))
                .andExpect(content().string(containsString("Minhas Encomendas de Arte")));
    }

    @Test
    @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
    @DisplayName("Deve carregar tela de solicitacao de comissao para modalidade existente")
    void deveCarregarFormularioDeSolicitacao() throws Exception {
        mockMvc.perform(get("/comissoes/solicitar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("cliente/solicitar"))
                .andExpect(model().attributeExists("tipo"))
                .andExpect(model().attributeExists("solicitacaoDto"))
                .andExpect(content().string(containsString("Briefing e Instruções")));
    }

    @Test
    @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
    @DisplayName("Deve redirecionar com erro quando modalidade nao existir em submissao invalida sem gerar 500")
    void deveTratarSolicitacaoComTipoInexistenteOuNuloSemErro500() throws Exception {
        mockMvc.perform(post("/comissoes/solicitar")
                        .with(csrf())
                        .param("tipoComissaoId", "99999")
                        .param("briefing", "curto"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("erro"));
    }

    @Test
    @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
    @DisplayName("Deve reexibir formulario com erros de validacao e tipo preenchido quando briefing for invalido")
    void deveReexibirFormularioSolicitacaoQuandoBriefingInvalidoMasTipoValido() throws Exception {
        mockMvc.perform(post("/comissoes/solicitar")
                        .with(csrf())
                        .param("tipoComissaoId", "1")
                        .param("briefing", "curto"))
                .andExpect(status().isOk())
                .andExpect(view().name("cliente/solicitar"))
                .andExpect(model().attributeExists("tipo"))
                .andExpect(model().hasErrors());
    }

    @Test
    @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
    @DisplayName("Deve redirecionar com erro quando pedido de avaliacao for inexistente sem gerar 500")
    void deveTratarAvaliacaoDePedidoInexistenteSemErro500() throws Exception {
        mockMvc.perform(post("/comissoes/99999/avaliar")
                        .with(csrf())
                        .param("pedidoId", "99999")
                        .param("nota", "5")
                        .param("comentario", "Excelente!"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                .andExpect(flash().attributeExists("erro"));
    }

    @Test
    @WithMockUser(username = "admin@artificio.local", roles = {"ADMIN"})
    @DisplayName("Deve negar (403 Forbidden) acesso de administrador ao painel de pedidos de cliente")
    void deveNegarAcessoDeAdminAoPainelCliente() throws Exception {
        mockMvc.perform(get("/painel/cliente/pedidos"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
    @DisplayName("Deve impedir artista de abrir tela de solicitacao para seu proprio servico")
    void deveImpedirArtistaDeAbrirFormularioDeAutocontratacao() throws Exception {
        // Modalidade 1 pertence à artista anna@artificio.local
        mockMvc.perform(get("/comissoes/solicitar/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/artistas/2"))
                .andExpect(flash().attributeExists("erro"));
    }

    @Test
    @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
    @DisplayName("Deve rejeitar submissao de solicitacao para o proprio artista com flash de erro")
    void deveRejeitarSubmissaoDeAutocontratacaoPorArtista() throws Exception {
        mockMvc.perform(post("/comissoes/solicitar")
                        .with(csrf())
                        .param("tipoComissaoId", "1")
                        .param("briefing", "Briefing longo e detalhado para encomendar de mim mesma"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"))
                .andExpect(flash().attributeExists("erro"));
    }

    @Test
    @WithMockUser(username = "cristiano@artificio.local", roles = {"ARTISTA"})
    @DisplayName("Deve permitir que um artista contrate outro artista sem bloqueio")
    void devePermitirQueArtistaContrateOutroArtista() throws Exception {
        // Modalidade 1 pertence à Anna, Cristiano é outro artista
        mockMvc.perform(get("/comissoes/solicitar/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("cliente/solicitar"))
                .andExpect(model().attributeExists("tipo"));

        mockMvc.perform(post("/comissoes/solicitar")
                        .with(csrf())
                        .param("tipoComissaoId", "1")
                        .param("briefing", "Briefing detalhado para a colega Anna encomendado por Cristiano"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                .andExpect(flash().attributeExists("sucesso"));
    }
}
