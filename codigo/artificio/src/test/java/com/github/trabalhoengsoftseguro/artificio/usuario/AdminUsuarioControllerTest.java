package com.github.trabalhoengsoftseguro.artificio.usuario;

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
class AdminUsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Deve redirecionar usuario anonimo para o login ao tentar acessar painel administrativo")
    void deveRedirecionarAnonimoParaLogin() throws Exception {
        mockMvc.perform(get("/admin/usuarios"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
    @DisplayName("Deve negar acesso (403 Forbidden) para cliente comum no painel administrativo")
    void deveNegarAcessoParaClienteComum() throws Exception {
        mockMvc.perform(get("/admin/usuarios"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
    @DisplayName("Deve negar acesso (403 Forbidden) para artista no painel administrativo")
    void deveNegarAcessoParaArtista() throws Exception {
        mockMvc.perform(get("/admin/usuarios"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@artificio.local", roles = {"ADMIN"})
    @DisplayName("Deve autorizar administrador a listar todos os usuarios")
    void devePermitirAdminListarUsuarios() throws Exception {
        mockMvc.perform(get("/admin/usuarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/usuarios"))
                .andExpect(model().attributeExists("usuarios"))
                .andExpect(content().string(containsString("Supervisão de Usuários")));
    }

    @Test
    @WithMockUser(username = "admin@artificio.local", roles = {"ADMIN"})
    @DisplayName("Deve permitir administrador promover cliente para artista")
    void devePermitirAdminPromoverCliente() throws Exception {
        // Cria um cliente temporário exclusivo para o teste de promoção
        Usuario novoCliente = new Usuario(
                "Teste Promoção",
                "promover.teste@artificio.local",
                "SenhaSegura#123",
                Role.ROLE_CLIENTE,
                "Biografia de teste",
                null
        );
        novoCliente = usuarioRepository.save(novoCliente);

        mockMvc.perform(post("/admin/usuarios/" + novoCliente.getId() + "/promover")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/usuarios"))
                .andExpect(flash().attributeExists("sucesso"));
    }

    @Test
    @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
    @DisplayName("Deve negar (403) tentativa de cliente comum promover usuarios")
    void deveNegarPromocaoParaCliente() throws Exception {
        mockMvc.perform(post("/admin/usuarios/6/promover")
                        .with(csrf()))
                .andExpect(status().isForbidden());
    }
}
