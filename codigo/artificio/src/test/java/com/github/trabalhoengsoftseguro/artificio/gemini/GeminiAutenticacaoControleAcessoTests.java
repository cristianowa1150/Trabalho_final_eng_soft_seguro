package com.github.trabalhoengsoftseguro.artificio.gemini;

import com.github.trabalhoengsoftseguro.artificio.artista.TipoComissao;
import com.github.trabalhoengsoftseguro.artificio.usuario.Role;
import com.github.trabalhoengsoftseguro.artificio.usuario.TokenAtivacao;
import com.github.trabalhoengsoftseguro.artificio.usuario.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Testes Gemini: Autenticação, Controle de Acesso (RBAC), Escalada de Privilégios e CSRF")
public class GeminiAutenticacaoControleAcessoTests extends GeminiBaseTest {

    // ------------------------------------------------------------------------------------------------
    // 1. CADASTRO E AUTENTICAÇÃO SOB SITUAÇÕES BIZANTINAS
    // ------------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Testes Gemini: Cadastro e Autenticação sob Situações Bizantinas")
    class CadastroEAutenticacaoBordaTests {

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar cadastro com nome gigantesco (5.000 caracteres) através da validação @Size")
        void testCadastroComNomeGigantesco() throws Exception {
            log.info("Elaborado pelo Gemini: Testando cadastro com nome com comprimento exorbitante.");
            String nomeGigante = "A".repeat(5000);

            mockMvc.perform(post("/cadastro")
                    .with(csrf())
                    .param("nome", nomeGigante)
                    .param("email", "teste.gigante@artificio.local")
                    .param("senha", "SenhaSegura123")
                    .param("role", "ROLE_CLIENTE")
                    .param("bio", "Bio comum")
                    .param("chavePix", ""))
                    .andExpect(status().isOk())
                    .andExpect(view().name("cadastro"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrors("cadastroDto", "nome"));
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar cadastro com nome de apenas 1 caractere")
        void testCadastroComNomeDeUmCaractere() throws Exception {
            log.info("Elaborado pelo Gemini: Testando cadastro com nome inferior ao limite mínimo.");
            mockMvc.perform(post("/cadastro")
                    .with(csrf())
                    .param("nome", "X")
                    .param("email", "x@artificio.local")
                    .param("senha", "SenhaSegura123")
                    .param("role", "ROLE_CLIENTE"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("cadastro"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrors("cadastroDto", "nome"));
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar e-mails em formatos anômalos e bizarros")
        void testCadastroComEmailAnomalo() throws Exception {
            log.info("Elaborado pelo Gemini: Testando rejeição de e-mails com formatos bizarros.");
            mockMvc.perform(post("/cadastro")
                    .with(csrf())
                    .param("nome", "Usuario Formato")
                    .param("email", "usuario@@dominio..invalido")
                    .param("senha", "SenhaSegura123")
                    .param("role", "ROLE_CLIENTE"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("cadastro"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrors("cadastroDto", "email"));
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar cadastro com senha de apenas 2 caracteres")
        void testCadastroComSenhaCurtaDemais() throws Exception {
            log.info("Elaborado pelo Gemini: Testando senha de tamanho insuficiente.");
            mockMvc.perform(post("/cadastro")
                    .with(csrf())
                    .param("nome", "Usuario Curto")
                    .param("email", "curto@artificio.local")
                    .param("senha", "12")
                    .param("role", "ROLE_CLIENTE"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("cadastro"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrors("cadastroDto", "senha"));
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar biografia de 10.000 caracteres excedendo o limite de 2.000")
        void testCadastroComBioGigantesca() throws Exception {
            log.info("Elaborado pelo Gemini: Testando biografia com texto exagerado.");
            String bioGigante = "B".repeat(10000);

            mockMvc.perform(post("/cadastro")
                    .with(csrf())
                    .param("nome", "Usuario Bio Longa")
                    .param("email", "bio.longa@artificio.local")
                    .param("senha", "SenhaValida123")
                    .param("role", "ROLE_CLIENTE")
                    .param("bio", bioGigante))
                    .andExpect(status().isOk())
                    .andExpect(view().name("cadastro"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrors("cadastroDto", "bio"));
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve tratar confirmação de e-mail com token vazio sem lançar 500")
        void testConfirmacaoEmailComTokenVazio() throws Exception {
            log.info("Elaborado pelo Gemini: Testando confirmação de e-mail com token vazio.");
            mockMvc.perform(get("/confirmar-email").param("token", ""))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"))
                    .andExpect(flash().attribute("erro", "Token de confirmação não informado."));
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve tratar confirmação de e-mail com token espúrio inexistente")
        void testConfirmacaoEmailComTokenEspurio() throws Exception {
            log.info("Elaborado pelo Gemini: Testando confirmação de e-mail com token aleatório inventado.");
            mockMvc.perform(get("/confirmar-email").param("token", "token-completamente-inventado-xyz-987"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"))
                    .andExpect(flash().attributeExists("erro"));
        }
    }

    // ------------------------------------------------------------------------------------------------
    // 2. CONTROLE DE ACESSO, PAPÉIS (ROLES) E DEGRADAÇÃO ELEGANTE
    // ------------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Testes Gemini: Segurança de Rotas, Isolamento de Perfis e Permissões")
    class ControleDeAcessoERolesDefensivoTests {

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve redirecionar anônimo tentando acessar painel do cliente para tela de login")
        void testAnonimoBarradoNoPainelCliente() throws Exception {
            log.info("Elaborado pelo Gemini: Testando proteção de rota de cliente contra anônimo.");
            mockMvc.perform(get("/painel/cliente/pedidos"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"));
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve redirecionar anônimo tentando acessar painel do artista para tela de login")
        void testAnonimoBarradoNoPainelArtista() throws Exception {
            log.info("Elaborado pelo Gemini: Testando proteção de rota de artista contra anônimo.");
            mockMvc.perform(get("/painel/artista/servicos"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"));
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve redirecionar anônimo tentando acessar área de administração para tela de login")
        void testAnonimoBarradoNaAdministracao() throws Exception {
            log.info("Elaborado pelo Gemini: Testando proteção de rota de admin contra anônimo.");
            mockMvc.perform(get("/admin/usuarios"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve retornar HTTP 403 Forbidden quando cliente tentar acessar serviços de artista")
        void testClienteTentandoAcessarPainelArtista() throws Exception {
            log.info("Elaborado pelo Gemini: Testando isolamento horizontal/vertical: cliente acessando painel artista.");
            mockMvc.perform(get("/painel/artista/servicos"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve retornar HTTP 403 Forbidden quando artista tentar acessar moderação de administração")
        void testArtistaTentandoAcessarAdmin() throws Exception {
            log.info("Elaborado pelo Gemini: Testando artista acessando área administrativa.");
            mockMvc.perform(get("/admin/usuarios"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "admin@artificio.local", roles = {"ADMIN"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir administrador de contratar serviços por regra de segurança (403 Forbidden)")
        void testAdminImpedidoDeContratarServicos() throws Exception {
            log.info("Elaborado pelo Gemini: Testando regra de segurança impedindo administrador de acessar rotas de contratação de comissões.");
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = tipoComissaoRepository.findByArtistaIdAndAtivoTrue(anna.getId()).get(0);

            mockMvc.perform(get("/comissoes/solicitar/" + tipo.getId()))
                    .andExpect(status().isForbidden());
        }
    }

    // ------------------------------------------------------------------------------------------------
    // 3. TENTATIVAS DE ESCALADA DE PRIVILÉGIOS E SEGURANÇA DE AUTO-CADASTRO
    // ------------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Testes Gemini: Escalada de Privilégios e Burlas de Cadastro")
    class EscaladaDePrivilegiosEAutoCadastroTests {

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar auto-cadastro malicioso injetando ROLE_ADMIN")
        void testTentativaEscaladaDePrivilegiosAdminNoCadastro() throws Exception {
            log.info("Elaborado pelo Gemini: Testando tentativa de um usuário comum se auto-cadastrar como ROLE_ADMIN.");
            mockMvc.perform(post("/cadastro")
                            .with(csrf())
                            .param("nome", "Hacker Tentando Admin")
                            .param("email", "fakeadmin@artificio.local")
                            .param("senha", "SenhaSegura123")
                            .param("role", "ROLE_ADMIN"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("cadastro"))
                    .andExpect(model().attribute("erro", "Não é permitido cadastrar administradores diretamente"));
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar cadastro com e-mail duplicado de usuário já registrado")
        void testTentativaCadastroEmailDuplicado() throws Exception {
            log.info("Elaborado pelo Gemini: Testando tentativa de cadastro com e-mail já existente.");
            mockMvc.perform(post("/cadastro")
                            .with(csrf())
                            .param("nome", "Clone da Anna")
                            .param("email", "anna@artificio.local")
                            .param("senha", "SenhaSegura123")
                            .param("role", "ROLE_CLIENTE"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("cadastro"))
                    .andExpect(model().attribute("erro", "E-mail já cadastrado na plataforma"));
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve normalizar e-mail em minúsculas (case-insensitivity) ao registrar")
        void testCadastroEmailCaseInsensitiveNormalizacao() throws Exception {
            log.info("Elaborado pelo Gemini: Testando normalização de e-mails com letras maiúsculas.");
            mockMvc.perform(post("/cadastro")
                            .with(csrf())
                            .param("nome", "Usuario Maiusculo")
                            .param("email", "MAIUSCULO.USER@ARTIFICIO.LOCAL")
                            .param("senha", "SenhaSegura123")
                            .param("role", "ROLE_CLIENTE"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"));

            assertThat(usuarioRepository.existsByEmail("maiusculo.user@artificio.local")).isTrue();
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve impedir reutilização (replay) do mesmo token de confirmação de e-mail")
        void testReplayDeTokenDeConfirmacao() throws Exception {
            log.info("Elaborado pelo Gemini: Testando prevenção contra replay de token de ativação.");
            Usuario novo = new Usuario("Usuario Replay", "replay@artificio.local", "hash123", Role.ROLE_CLIENTE, "", "");
            novo.setEmailConfirmado(false);
            usuarioRepository.save(novo);

            String tokenStr = UUID.randomUUID().toString();
            TokenAtivacao token = new TokenAtivacao(tokenStr, novo, LocalDateTime.now().plusHours(1));
            tokenAtivacaoRepository.save(token);

            // Primeira confirmação: sucesso
            mockMvc.perform(get("/confirmar-email").param("token", tokenStr))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"))
                    .andExpect(flash().attributeExists("sucesso"));

            // Segunda confirmação com o mesmo token: rejeição (token consumido e excluído)
            mockMvc.perform(get("/confirmar-email").param("token", tokenStr))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"))
                    .andExpect(flash().attribute("erro", "Token de confirmação inválido ou não encontrado"));
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar confirmação com token já expirado no tempo")
        void testTokenDeConfirmacaoExpirado() throws Exception {
            log.info("Elaborado pelo Gemini: Testando token de confirmação expirado.");
            Usuario novo = new Usuario("Usuario Expirado", "expirado@artificio.local", "hash123", Role.ROLE_CLIENTE, "", "");
            novo.setEmailConfirmado(false);
            usuarioRepository.save(novo);

            String tokenStr = UUID.randomUUID().toString();
            TokenAtivacao token = new TokenAtivacao(tokenStr, novo, LocalDateTime.now().minusHours(2));
            tokenAtivacaoRepository.save(token);

            mockMvc.perform(get("/confirmar-email").param("token", tokenStr))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"))
                    .andExpect(flash().attribute("erro", "O link de confirmação expirou. Por favor, realize um novo cadastro."));
        }
    }

    // ------------------------------------------------------------------------------------------------
    // 4. PROTEÇÃO CSRF EM TODOS OS ENDPOINTS MUTÁVEIS (POST) DO SISTEMA
    // ------------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Testes Gemini: Garantia de Proteção CSRF em Endpoints de Modificação de Estado")
    class ProtecaoCsrfEmTodosOsEndpointsTests {

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve barrar POST /cadastro sem token CSRF com 403 Forbidden")
        void testCadastroSemCsrfRetorna403() throws Exception {
            log.info("Elaborado pelo Gemini: Testando CSRF em /cadastro.");
            mockMvc.perform(post("/cadastro")
                            .param("nome", "Hacker CSRF")
                            .param("email", "csrf@artificio.local")
                            .param("senha", "Senha123")
                            .param("role", "ROLE_CLIENTE"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve barrar criação de serviço sem CSRF com 403 Forbidden")
        void testNovoServicoSemCsrfRetorna403() throws Exception {
            log.info("Elaborado pelo Gemini: Testando CSRF em criação de serviço.");
            mockMvc.perform(post("/painel/artista/servicos/novo")
                            .param("titulo", "Serviço Forjado")
                            .param("categoria", "Aquarela")
                            .param("precoBase", "100.00")
                            .param("prazoEstimadoDias", "5"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve barrar solicitação de comissão sem CSRF com 403 Forbidden")
        void testSolicitacaoComissaoSemCsrfRetorna403() throws Exception {
            log.info("Elaborado pelo Gemini: Testando CSRF em solicitação de comissão.");
            mockMvc.perform(post("/comissoes/solicitar")
                            .param("tipoComissaoId", UUID.randomUUID().toString())
                            .param("briefing", "Briefing forjado sem token"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve barrar confirmação de pagamento sem CSRF com 403 Forbidden")
        void testConfirmarPagamentoSemCsrfRetorna403() throws Exception {
            log.info("Elaborado pelo Gemini: Testando CSRF em confirmação de pagamento.");
            mockMvc.perform(post("/comissoes/" + UUID.randomUUID() + "/confirmar-pagamento"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "admin@artificio.local", roles = {"ADMIN"})
        @DisplayName("Elaborado pelo Gemini: Deve barrar promoção de usuário sem CSRF com 403 Forbidden")
        void testPromocaoAdminSemCsrfRetorna403() throws Exception {
            log.info("Elaborado pelo Gemini: Testando CSRF em promoção de usuário por administrador.");
            mockMvc.perform(post("/admin/usuarios/" + UUID.randomUUID() + "/promover"))
                    .andExpect(status().isForbidden());
        }
    }
}
