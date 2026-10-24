package com.github.trabalhoengsoftseguro.artificio.usuario;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Deve renderizar a tela de login com status 200 para usuarios anonimos")
    void deveRenderizarTelaDeLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"))
                .andExpect(content().string(containsString("Acesse sua Conta")));
    }

    @Test
    @DisplayName("Deve exibir facilitador de credenciais de teste em ambiente padrao de avaliacao")
    void deveExibirCredenciaisDemoEmAmbientePadrao() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("exibirCredenciaisDemo", true))
                .andExpect(content().string(containsString("Acesso Rápido para Testes")))
                .andExpect(content().string(containsString("Contas de Teste")));
    }

    @Test
    @DisplayName("Deve renderizar formulario de cadastro com status 200")
    void deveRenderizarTelaDeCadastro() throws Exception {
        mockMvc.perform(get("/cadastro"))
                .andExpect(status().isOk())
                .andExpect(view().name("cadastro"))
                .andExpect(content().string(containsString("Junte-se ao Artifício")));
    }

    @Test
    @DisplayName("Deve cadastrar novo cliente com sucesso e redirecionar para tela de login")
    void deveCadastrarNovoClienteComSucesso() throws Exception {
        mockMvc.perform(post("/cadastro")
                        .with(csrf())
                        .param("nome", "Novo Usuario")
                        .param("email", "novo.cliente@artificio.local")
                        .param("senha", "senhaSegura123")
                        .param("role", "ROLE_CLIENTE"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("sucesso"));
    }

    @Autowired
    private TokenAtivacaoRepository tokenAtivacaoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Deve rejeitar cadastro com dados invalidos e exibir mensagens de erro")
    void deveRejeitarCadastroInvalido() throws Exception {
        mockMvc.perform(post("/cadastro")
                        .with(csrf())
                        .param("nome", "")
                        .param("email", "email-invalido")
                        .param("senha", "123")
                        .param("role", "ROLE_CLIENTE"))
                .andExpect(status().isOk())
                .andExpect(view().name("cadastro"))
                .andExpect(model().hasErrors());
    }

    @Test
    @DisplayName("Deve confirmar email com token valido e redirecionar para login com sucesso")
    void deveConfirmarEmailComTokenValido() throws Exception {
        CadastroDto dto = new CadastroDto("Usuario Confirma", "confirma@artificio.local", "senhaSegura123", Role.ROLE_CLIENTE, null, null);
        Usuario usuario = usuarioService.registrarUsuario(dto);

        TokenAtivacao token = tokenAtivacaoRepository.findAll().stream()
                .filter(t -> t.getUsuario().getId().equals(usuario.getId()))
                .findFirst()
                .orElseThrow();

        mockMvc.perform(get("/confirmar-email")
                        .param("token", token.getToken()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attribute("sucesso", "E-mail confirmado com sucesso! Sua conta está ativa para login."));
    }

    @Test
    @DisplayName("Deve redirecionar para login com mensagem de erro quando token for invalido")
    void deveRedirecionarComErroQuandoTokenInvalido() throws Exception {
        mockMvc.perform(get("/confirmar-email")
                        .param("token", "token-inexistente-xyz"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("erro"));
    }
}

