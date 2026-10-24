package com.github.trabalhoengsoftseguro.artificio.gemini;

import com.github.trabalhoengsoftseguro.artificio.artista.TipoComissao;
import com.github.trabalhoengsoftseguro.artificio.usuario.Role;
import com.github.trabalhoengsoftseguro.artificio.usuario.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Testes Gemini: Interface Web Thymeleaf (Anti-XSS) e Trilha Forense de Auditoria (Não-Repúdio)")
public class GeminiWebAuditoriaTests extends GeminiBaseTest {

    // ------------------------------------------------------------------------------------------------
    // 1. TESTES DE INTERFACE WEB (HTML THYMELEAF, LAYOUT E SEGURANÇA DE RENDERIZAÇÃO)
    // ------------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Testes Gemini: Testes da Interface Web (Thymeleaf, Escapamento XSS e Layout)")
    class InterfaceWebHtmlRenderizacaoESegurancaTests {

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve renderizar layout base completo para visitante anônimo com botões de login e cadastro")
        void testRenderizacaoLayoutBaseParaAnonimo() throws Exception {
            log.info("Elaborado pelo Gemini: Verificando HTML renderizado para visitante anônimo.");
            mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Artifício")))
                    .andExpect(content().string(containsString("Catálogo")))
                    .andExpect(content().string(containsString("Entrar")))
                    .andExpect(content().string(containsString("Cadastrar-se")))
                    .andExpect(content().string(containsString("Contas de Teste")));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve renderizar layout específico para Cliente com menu de Minhas Encomendas e botão de Sair")
        void testRenderizacaoLayoutBaseParaClienteAutenticado() throws Exception {
            log.info("Elaborado pelo Gemini: Verificando HTML renderizado para perfil CLIENTE.");
            mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Minhas Encomendas")))
                    .andExpect(content().string(containsString("eduardo@artificio.local")))
                    .andExpect(content().string(containsString("Sair")));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve renderizar layout específico para Artista com menus de Portfólio, Serviços e Pedidos")
        void testRenderizacaoLayoutBaseParaArtistaAutenticado() throws Exception {
            log.info("Elaborado pelo Gemini: Verificando HTML renderizado para perfil ARTISTA.");
            mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Pedidos Recebidos")))
                    .andExpect(content().string(containsString("Meu Portfólio")))
                    .andExpect(content().string(containsString("Meus Serviços")));
        }

        @Test
        @WithMockUser(username = "admin@artificio.local", roles = {"ADMIN"})
        @DisplayName("Elaborado pelo Gemini: Deve renderizar layout específico para Administrador com menu Usuários & Moderação")
        void testRenderizacaoLayoutBaseParaAdminAutenticado() throws Exception {
            log.info("Elaborado pelo Gemini: Verificando HTML renderizado para perfil ADMIN.");
            mockMvc.perform(get("/"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Moderação")));
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve garantir escapamento HTML contra Stored XSS na renderização de biografias de artistas")
        void testEscapeHtmlContraStoredXssEmBio() throws Exception {
            log.info("Elaborado pelo Gemini: Verificando se tags HTML/scripts são devidamente escapadas pelo Thymeleaf na vitrine do artista.");
            Usuario artistaXss = new Usuario("Artista Script", "script.artist@artificio.local", "hash123",
                    Role.ROLE_ARTISTA, "<script>alert(1)</script>", "");
            artistaXss = usuarioRepository.save(artistaXss);

            mockMvc.perform(get("/artistas/" + artistaXss.getId()))
                    .andExpect(status().isOk())
                    // Deve conter a entidade escapada &lt;script&gt; e NÃO a tag executável <script>
                    .andExpect(content().string(containsString("&lt;script&gt;alert(1)&lt;/script&gt;")))
                    .andExpect(content().string(not(containsString("<script>alert(1)</script>"))));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Tela de solicitar comissão deve renderizar card do serviço com preço, prazo e formulário")
        void testRenderizacaoTelaSolicitacaoComissao() throws Exception {
            log.info("Elaborado pelo Gemini: Verificando renderização da interface web de contratação.");
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = tipoComissaoRepository.findByArtistaIdAndAtivoTrue(anna.getId()).get(0);

            mockMvc.perform(get("/comissoes/solicitar/" + tipo.getId()))
                    .andExpect(status().isOk())
                    .andExpect(view().name("cliente/solicitar"))
                    .andExpect(content().string(containsString("Briefing e Instruções")))
                    .andExpect(content().string(containsString("Preço Base:")))
                    .andExpect(content().string(containsString("Enviar Solicitação de Encomenda")));
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Tela de login deve renderizar campos de e-mail, senha e links de recuperação/cadastro")
        void testRenderizacaoTelaLogin() throws Exception {
            log.info("Elaborado pelo Gemini: Verificando renderização da interface de login.");
            mockMvc.perform(get("/login"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("login"))
                    .andExpect(content().string(containsString("Acesse sua Conta")))
                    .andExpect(content().string(containsString("E-mail")))
                    .andExpect(content().string(containsString("Senha")))
                    .andExpect(content().string(containsString("Cadastre-se aqui")));
        }
    }

    // ------------------------------------------------------------------------------------------------
    // 2. AUDITORIA FORENSE, NÃO-REPÚDIO E ENTRADAS ANÔMALAS (OWASP)
    // ------------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Testes Gemini: Auditoria Forense e Situações Anômalas na Trilha de Auditoria (OWASP)")
    class AuditoriaForenseESituacoesAnomalasTests {

        @Test
        @WithMockUser(username = "admin@artificio.local", roles = {"ADMIN"})
        @DisplayName("Elaborado pelo Gemini: Deve suportar busca na auditoria com string gigantesca (20.000 caracteres) sem erro 500")
        void testBuscaAuditoriaComStringGigantesca() throws Exception {
            log.info("Elaborado pelo Gemini: Testando busca anômala na auditoria com payload de 20.000 caracteres.");
            String buscaGigante = "A".repeat(20000);

            mockMvc.perform(get("/admin/auditoria").param("busca", buscaGigante))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/auditoria"))
                    .andExpect(model().attributeExists("pagina"));
        }

        @Test
        @WithMockUser(username = "admin@artificio.local", roles = {"ADMIN"})
        @DisplayName("Elaborado pelo Gemini: Deve neutralizar Reflected XSS no termo de busca da auditoria escapando tags HTML")
        void testReflectedXssNoTermoDeBuscaAuditoria() throws Exception {
            log.info("Elaborado pelo Gemini: Testando tentativa de XSS refletido no parâmetro de busca da auditoria.");
            String xssPayload = "<script>alert('xss-auditoria')</script>";

            mockMvc.perform(get("/admin/auditoria").param("busca", xssPayload))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/auditoria"))
                    .andExpect(content().string(containsString("&lt;script&gt;alert(&#39;xss-auditoria&#39;)&lt;/script&gt;")))
                    .andExpect(content().string(not(containsString("<script>alert('xss-auditoria')</script>"))));
        }

        @Test
        @WithMockUser(username = "admin@artificio.local", roles = {"ADMIN"})
        @DisplayName("Elaborado pelo Gemini: Deve processar com segurança busca contendo payload SQL Injection sem lançar erro 500")
        void testSqlInjectionNoTermoDeBuscaAuditoria() throws Exception {
            log.info("Elaborado pelo Gemini: Testando tentativa de SQL Injection no filtro de auditoria.");
            String sqliPayload = "' OR '1'='1' UNION SELECT * FROM usuarios --";

            mockMvc.perform(get("/admin/auditoria").param("busca", sqliPayload))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/auditoria"))
                    .andExpect(model().attributeExists("revisoes"));
        }

        @Test
        @WithMockUser(username = "admin@artificio.local", roles = {"ADMIN"})
        @DisplayName("Elaborado pelo Gemini: Deve suportar paginação negativa com degradação segura sem estourar exceção")
        void testPaginacaoNegativaAuditoria() throws Exception {
            log.info("Elaborado pelo Gemini: Testando paginação com valores negativos em auditoria.");
            mockMvc.perform(get("/admin/auditoria")
                            .param("pagina", "-5")
                            .param("tamanho", "-20"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/auditoria"))
                    .andExpect(model().attributeExists("pagina"));
        }

        @Test
        @WithMockUser(username = "admin@artificio.local", roles = {"ADMIN"})
        @DisplayName("Elaborado pelo Gemini: Deve suportar página além do limite total retornando página vazia com segurança")
        void testPaginacaoAlemDoLimiteAuditoria() throws Exception {
            log.info("Elaborado pelo Gemini: Testando página de auditoria com índice astronômico.");
            mockMvc.perform(get("/admin/auditoria")
                            .param("pagina", "999999")
                            .param("tamanho", "50"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/auditoria"))
                    .andExpect(model().attributeExists("pagina"));
        }

        @Test
        @WithMockUser(username = "admin@artificio.local", roles = {"ADMIN"})
        @DisplayName("Elaborado pelo Gemini: Deve retornar 405 Method Not Allowed para POST em /admin/auditoria (exclusivo GET)")
        void testPostEmRotaDeAuditoriaRetorna405() throws Exception {
            log.info("Elaborado pelo Gemini: Testando método HTTP inválido POST na rota de auditoria.");
            mockMvc.perform(post("/admin/auditoria").with(csrf()))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @WithMockUser(username = "admin@artificio.local", roles = {"ADMIN"})
        @DisplayName("Elaborado pelo Gemini: Deve retornar 405 Method Not Allowed para DELETE em /admin/auditoria (tentativa de expurgo)")
        void testDeleteEmRotaDeAuditoriaRetorna405() throws Exception {
            log.info("Elaborado pelo Gemini: Testando tentativa indevida de DELETE na trilha forense de auditoria.");
            mockMvc.perform(delete("/admin/auditoria").with(csrf()))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @WithMockUser(username = "admin@artificio.local", roles = {"ADMIN"})
        @DisplayName("Elaborado pelo Gemini: Deve renderizar interface de auditoria com cabeçalhos de Não-Repúdio e tabela de revisões")
        void testRenderizacaoInterfaceAuditoriaComNaoRepudio() throws Exception {
            log.info("Elaborado pelo Gemini: Verificando HTML renderizado da tela de auditoria forense.");
            mockMvc.perform(get("/admin/auditoria"))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("Trilha de Auditoria")))
                    .andExpect(content().string(containsString("Histórico de registros")))
                    .andExpect(content().string(containsString("Operação")))
                    .andExpect(content().string(containsString("Resumo do Estado")));
        }
    }
}
