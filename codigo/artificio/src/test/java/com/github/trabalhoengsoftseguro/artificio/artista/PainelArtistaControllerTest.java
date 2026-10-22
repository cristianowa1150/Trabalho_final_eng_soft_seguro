package com.github.trabalhoengsoftseguro.artificio.artista;

import com.github.trabalhoengsoftseguro.artificio.usuario.Usuario;
import com.github.trabalhoengsoftseguro.artificio.usuario.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class PainelArtistaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TipoComissaoRepository tipoComissaoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Deve barrar usuario anonimo tentando acessar o painel do artista")
    void deveBarrarUsuarioAnonimo() throws Exception {
        mockMvc.perform(get("/painel/artista/pedidos"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
    @DisplayName("Deve negar acesso (403) para cliente comum tentando acessar painel do artista")
    void deveNegarAcessoParaClienteComum() throws Exception {
        mockMvc.perform(get("/painel/artista/pedidos"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "cristiano@artificio.local", roles = {"ARTISTA"})
    @DisplayName("Deve autorizar artista autenticado a visualizar seus pedidos recebidos")
    void deveAutorizarArtistaAcessarPedidos() throws Exception {
        mockMvc.perform(get("/painel/artista/pedidos"))
                .andExpect(status().isOk())
                .andExpect(view().name("artista/pedidos"))
                .andExpect(model().attributeExists("pedidos"))
                .andExpect(content().string(containsString("Painel de Pedidos Recebidos")));
    }

    @Test
    @WithMockUser(username = "cristiano@artificio.local", roles = {"ARTISTA"})
    @DisplayName("Deve renderizar a tela de gerenciamento de portfolio do artista logado")
    void deveRenderizarPortfolioDoArtista() throws Exception {
        mockMvc.perform(get("/painel/artista/portfolio"))
                .andExpect(status().isOk())
                .andExpect(view().name("artista/portfolio"))
                .andExpect(model().attributeExists("obras"))
                .andExpect(model().attributeExists("novaObraDto"));
    }

    @Test
    @WithMockUser(username = "cristiano@artificio.local", roles = {"ARTISTA"})
    @DisplayName("Deve listar todos os servicos inclusive pausados no painel de gestao")
    void deveListarTodosOsServicosInclusivePausadosNoPainel() throws Exception {
        Usuario cristiano = usuarioRepository.findByEmail("cristiano@artificio.local").orElseThrow();
        TipoComissao servicoPausado = new TipoComissao("Chibi Especial Pausado", "Descricao", "Pixel Art", new BigDecimal("80.00"), 5, cristiano);
        servicoPausado.setAtivo(false);
        servicoPausado = tipoComissaoRepository.save(servicoPausado);

        mockMvc.perform(get("/painel/artista/servicos"))
                .andExpect(status().isOk())
                .andExpect(view().name("artista/servicos"))
                .andExpect(model().attributeExists("servicos"))
                .andExpect(content().string(containsString("Chibi Especial Pausado")))
                .andExpect(content().string(containsString("Pausado")))
                .andExpect(content().string(containsString("Ativar")));
    }

    @Test
    @WithMockUser(username = "cristiano@artificio.local", roles = {"ARTISTA"})
    @DisplayName("Deve alternar status de servico ativo para pausado via toggle")
    void deveAlternarStatusDoServicoParaPausado() throws Exception {
        Usuario cristiano = usuarioRepository.findByEmail("cristiano@artificio.local").orElseThrow();
        TipoComissao servico = new TipoComissao("Emote Discord Teste", "Descricao", "Pixel Art", new BigDecimal("50.00"), 2, cristiano);
        servico.setAtivo(true);
        servico = tipoComissaoRepository.save(servico);

        mockMvc.perform(post("/painel/artista/servicos/" + servico.getId() + "/toggle")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/painel/artista/servicos"))
                .andExpect(flash().attributeExists("sucesso"));

        TipoComissao atualizado = tipoComissaoRepository.findById(servico.getId()).orElseThrow();
        assertFalse(atualizado.isAtivo());
    }
}
