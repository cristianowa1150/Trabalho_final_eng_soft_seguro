package com.github.trabalhoengsoftseguro.artificio.auditoria;

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
class AdminAuditoriaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Deve barrar usuário anônimo tentando acessar trilha de auditoria e redirecionar para login")
    void deveBarrarAnonimoAoAcessarTrilhaAuditoria() throws Exception {
        mockMvc.perform(get("/admin/auditoria"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(username = "cliente@artificio.local", roles = {"CLIENTE"})
    @DisplayName("Deve negar acesso (403 Forbidden) para cliente comum na trilha de auditoria")
    void deveNegarAcessoParaCliente() throws Exception {
        mockMvc.perform(get("/admin/auditoria"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "artista@artificio.local", roles = {"ARTISTA"})
    @DisplayName("Deve negar acesso (403 Forbidden) para artista na trilha de auditoria")
    void deveNegarAcessoParaArtista() throws Exception {
        mockMvc.perform(get("/admin/auditoria"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@artificio.local", roles = {"ADMIN"})
    @DisplayName("Deve autorizar administrador autenticado a visualizar a trilha de auditoria forense")
    void devePermitirAdminVisualizarTrilhaAuditoria() throws Exception {
        mockMvc.perform(get("/admin/auditoria"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/auditoria"))
                .andExpect(model().attributeExists("revisoes"))
                .andExpect(model().attributeExists("pagina"))
                .andExpect(model().attributeExists("paginaAtual"))
                .andExpect(model().attributeExists("totalPaginas"))
                .andExpect(model().attributeExists("totalItens"))
                .andExpect(content().string(containsString("Trilha de Auditoria")));
    }

    @Test
    @WithMockUser(username = "admin@artificio.local", roles = {"ADMIN"})
    @DisplayName("Deve aceitar parametros de paginacao na consulta da trilha de auditoria")
    void deveAceitarParametrosPaginacao() throws Exception {
        mockMvc.perform(get("/admin/auditoria")
                        .param("pagina", "0")
                        .param("tamanho", "5"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/auditoria"))
                .andExpect(model().attribute("paginaAtual", 0));
    }

    @Test
    @WithMockUser(username = "admin@artificio.local", roles = {"ADMIN"})
    @DisplayName("Deve filtrar eventos da trilha de auditoria por termo de busca")
    void deveFiltrarTrilhaAuditoriaPorTermoDeBusca() throws Exception {
        mockMvc.perform(get("/admin/auditoria")
                        .param("busca", "Pedido"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/auditoria"))
                .andExpect(model().attribute("termoBusca", "Pedido"))
                .andExpect(model().attributeExists("pagina"));
    }
}
