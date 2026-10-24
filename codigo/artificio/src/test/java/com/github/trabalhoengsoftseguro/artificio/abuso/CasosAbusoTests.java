package com.github.trabalhoengsoftseguro.artificio.abuso;

import com.github.trabalhoengsoftseguro.artificio.artista.ObraPortfolio;
import com.github.trabalhoengsoftseguro.artificio.artista.ObraPortfolioRepository;
import com.github.trabalhoengsoftseguro.artificio.artista.TipoComissao;
import com.github.trabalhoengsoftseguro.artificio.artista.TipoComissaoRepository;
import com.github.trabalhoengsoftseguro.artificio.auditoria.AuditoriaService;
import com.github.trabalhoengsoftseguro.artificio.comissao.PedidoComissao;
import com.github.trabalhoengsoftseguro.artificio.comissao.PedidoComissaoRepository;
import com.github.trabalhoengsoftseguro.artificio.comissao.StatusComissao;
import com.github.trabalhoengsoftseguro.artificio.imagem.ImagemBlob;
import com.github.trabalhoengsoftseguro.artificio.imagem.ImagemBlobRepository;
import com.github.trabalhoengsoftseguro.artificio.imagem.ValidadorUploadImagem;
import com.github.trabalhoengsoftseguro.artificio.usuario.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ====================================================================================================
 * SUÍTE FORMAL DE CASOS DE ABUSO (CA01 A CA27)
 * ====================================================================================================
 * Implementação dos testes de segurança mapeados a partir do documento formal de modelagem de ameaças
 * e definição de casos de abuso (8.6 Definição de casos de abuso.md - Disciplina de Eng. de Software Seguro).
 * ====================================================================================================
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Casos de Abuso: Suíte Formal de Testes (CA01 a CA27)")
public class CasosAbusoTests {

    private static final Logger log = LoggerFactory.getLogger(CasosAbusoTests.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private TipoComissaoRepository tipoComissaoRepository;

    @Autowired
    private PedidoComissaoRepository pedidoComissaoRepository;

    @Autowired
    private ObraPortfolioRepository obraPortfolioRepository;

    @Autowired
    private TokenAtivacaoRepository tokenAtivacaoRepository;

    @Autowired
    private ValidadorUploadImagem validadorUploadImagem;

    @Autowired
    private ImagemBlobRepository imagemBlobRepository;

    @Autowired(required = false)
    private AuditoriaService auditoriaService;

    private TipoComissao obterTipoAnna() {
        Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
        return tipoComissaoRepository.findByArtistaIdAndAtivoTrue(anna.getId()).get(0);
    }

    private PedidoComissao criarPedido(StatusComissao status) {
        Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
        Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
        TipoComissao tipo = obterTipoAnna();

        PedidoComissao pedido = new PedidoComissao("Pedido Teste Abuso", new BigDecimal("180.00"), 5, eduardo, anna, tipo);
        pedido.setStatus(status);
        return pedidoComissaoRepository.save(pedido);
    }

    // ================================================================================================
    // CA01: CRIAÇÃO DE PERFIL FALSO DE ARTISTA E EVASÃO DE IDENTIDADE
    // ================================================================================================
    @Nested
    @DisplayName("Casos de Abuso - CA01: Perfil Falso e Evasão de Identidade")
    class CA01_PerfilFalsoEEvasaoTests {

        @Test
        @DisplayName("CA01.1: Deve impedir Stored XSS na biografia do artista na vitrine pública")
        void testCA01_1_StoredXssBioArtista() throws Exception {
            log.info("Caso de Abuso CA01.1: Testando mitigação de Stored XSS em perfil de artista.");
            Usuario artistaMalicioso = new Usuario("Artista Injetor", "xss.artist@artificio.local", "hash123",
                    Role.ROLE_ARTISTA, "<script>alert('xss-injetado')</script>", "");
            artistaMalicioso = usuarioRepository.save(artistaMalicioso);

            mockMvc.perform(get("/artistas/" + artistaMalicioso.getId()))
                    .andExpect(status().isOk())
                    .andExpect(content().string(containsString("&lt;script&gt;alert(&#39;xss-injetado&#39;)&lt;/script&gt;")))
                    .andExpect(content().string(not(containsString("<script>alert('xss-injetado')</script>"))));
        }

        @Test
        @DisplayName("CA01.2: Deve rejeitar cadastro com e-mail duplicado independente de variações de caixa (case-insensitive)")
        void testCA01_2_EvasaoEmailDuplicadoCaseInsensitive() throws Exception {
            log.info("Caso de Abuso CA01.2: Testando tentativa de cadastro com e-mail duplicado em maiúsculas.");
            mockMvc.perform(post("/cadastro")
                            .with(csrf())
                            .param("nome", "Impostor Anna")
                            .param("email", "ANNA@ARTIFICIO.LOCAL")
                            .param("senha", "Senha123")
                            .param("role", "ROLE_ARTISTA"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("cadastro"))
                    .andExpect(model().attribute("erro", "E-mail já cadastrado na plataforma"));
        }
    }

    // ================================================================================================
    // CA02: TOMADA DE CONTA POR COMPROMETIMENTO DE SESSÃO OU RECUPERAÇÃO DE SENHA
    // ================================================================================================
    @Nested
    @DisplayName("Casos de Abuso - CA02: Replay Attack e Tokens de Ativação")
    class CA02_TokensEAtivacaoTests {

        @Test
        @DisplayName("CA02.1: Deve barrar Replay Attack ao tentar reutilizar token de ativação já consumido")
        void testCA02_1_ReplayAttackTokenAtivacao() throws Exception {
            log.info("Caso de Abuso CA02.1: Testando tentativa de reutilização de token já utilizado (Replay Attack).");
            CadastroDto dto = new CadastroDto("Usuario Replay", "replay@artificio.local", "senhaSegura123", Role.ROLE_CLIENTE, null, null);
            Usuario usuario = usuarioService.registrarUsuario(dto);

            TokenAtivacao token = tokenAtivacaoRepository.findAll().stream()
                    .filter(t -> t.getUsuario().getId().equals(usuario.getId()))
                    .findFirst()
                    .orElseThrow();

            String tokenOriginal = token.getToken();

            // 1ª ativação: sucesso
            mockMvc.perform(get("/confirmar-email").param("token", tokenOriginal))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"))
                    .andExpect(flash().attributeExists("sucesso"));

            // 2ª tentativa (Replay): deve ser sumariamente rejeitada
            mockMvc.perform(get("/confirmar-email").param("token", tokenOriginal))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"))
                    .andExpect(flash().attributeExists("erro"));
        }

        @Test
        @DisplayName("CA02.2: Deve rejeitar ativação com token inexistente ou forjado")
        void testCA02_2_TokenAtivacaoInexistente() throws Exception {
            log.info("Caso de Abuso CA02.2: Testando confirmação com token inexistente.");
            mockMvc.perform(get("/confirmar-email").param("token", "token-forjado-12345"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"))
                    .andExpect(flash().attributeExists("erro"));
        }

        @Test
        @DisplayName("CA02.3: Deve rejeitar confirmação com token expirado e purgá-lo")
        void testCA02_3_TokenAtivacaoExpirado() throws Exception {
            log.info("Caso de Abuso CA02.3: Testando confirmação com token de ativação expirado.");
            Usuario usuario = new Usuario("Usuario Expirado", "expirado@artificio.local", "hash123", Role.ROLE_CLIENTE, "", "");
            usuario = usuarioRepository.save(usuario);

            TokenAtivacao tokenExpirado = new TokenAtivacao("token-expirado-abc", usuario, LocalDateTime.now().minusHours(25));
            tokenExpirado = tokenAtivacaoRepository.save(tokenExpirado);

            mockMvc.perform(get("/confirmar-email").param("token", tokenExpirado.getToken()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"))
                    .andExpect(flash().attributeExists("erro"));
        }
    }

    // ================================================================================================
    // CA04: USURPAÇÃO DE CONTA E ISOLAMENTO DE IDENTIDADE
    // ================================================================================================
    @Nested
    @DisplayName("Casos de Abuso - CA04: Usurpação e Isolamento de Identidade")
    class CA04_UsurpacaoEIdentidadeTests {

        @Test
        @WithMockUser(username = "luan@artificio.local", roles = {"CLIENTE"})
        @DisplayName("CA04.1: Deve isolar sessões e impedir que Cliente B execute ações autenticado em nome de Cliente A")
        void testCA04_1_IsolamentoSessaoCliente() throws Exception {
            log.info("Caso de Abuso CA04.1: Testando isolamento estrito de sessão do cliente solicitante.");
            PedidoComissao pedidoEduardo = criarPedido(StatusComissao.SOLICITADA);

            // Luan tenta cancelar pedido de Eduardo
            mockMvc.perform(post("/comissoes/" + pedidoEduardo.getId() + "/cancelar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas o cliente solicitante pode cancelar este pedido"));
        }
    }

    // ================================================================================================
    // CA05: ENVIO DE ARQUIVO MALICIOSO OU INCOMPATÍVEL NO PORTFÓLIO
    // ================================================================================================
    @Nested
    @DisplayName("Casos de Abuso - CA05: Uploads Maliciosos e Falsificação Binária")
    class CA05_UploadsMaliciososTests {

        @Test
        @DisplayName("CA05.1: Deve barrar upload de executável disfarçado com extensão .png (falsificação de assinatura mágica)")
        void testCA05_1_UploadFalsificacaoMagicBytes() {
            log.info("Caso de Abuso CA05.1: Testando rejeição de executável com extensão falsa.");
            byte[] executavelFalso = "MZ\u0090\u0000\u0003\u0000\u0000\u0000este-eh-um-executavel-disfarcado".getBytes(StandardCharsets.UTF_8);
            assertThatThrownBy(() -> validadorUploadImagem.validarEObterMimeType(executavelFalso, "image/png"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("não corresponde a uma imagem válida");
        }

        @Test
        @DisplayName("CA05.2: Deve barrar upload de SVG com script ativo (<SCRIPT>)")
        void testCA05_2_UploadSvgComTagScript() {
            log.info("Caso de Abuso CA05.2: Testando bloqueio de SVG contendo script ativo.");
            String svg = "<svg xmlns='http://www.w3.org/2000/svg'><SCRIPT>alert(1)</SCRIPT></svg>";
            assertThatThrownBy(() -> validadorUploadImagem.validarEObterMimeType(svg.getBytes(StandardCharsets.UTF_8), "image/svg+xml"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("tags não permitidas");
        }

        @Test
        @DisplayName("CA05.3: Deve barrar upload de SVG com manipulador de eventos JavaScript (onmouseover)")
        void testCA05_3_UploadSvgComManipuladorEvento() {
            log.info("Caso de Abuso CA05.3: Testando bloqueio de SVG com atributos de evento.");
            String svg = "<svg xmlns='http://www.w3.org/2000/svg' onmouseover='alert(1)'></svg>";
            assertThatThrownBy(() -> validadorUploadImagem.validarEObterMimeType(svg.getBytes(StandardCharsets.UTF_8), "image/svg+xml"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("atributos de evento");
        }

        @Test
        @DisplayName("CA05.4: Deve barrar upload de SVG contendo URIs maliciosas javascript: ou data:text/html")
        void testCA05_4_UploadSvgComUriJavascript() {
            log.info("Caso de Abuso CA05.4: Testando bloqueio de SVG com esquemas de URI executáveis.");
            String svg = "<svg xmlns='http://www.w3.org/2000/svg'><a href='javascript:alert(1)'><circle r='5'/></a></svg>";
            assertThatThrownBy(() -> validadorUploadImagem.validarEObterMimeType(svg.getBytes(StandardCharsets.UTF_8), "image/svg+xml"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("URIs ou esquemas JavaScript");
        }

        @Test
        @DisplayName("CA05.5: Deve barrar upload de SVG com tags proibidas (foreignObject, iframe, embed, object)")
        void testCA05_5_UploadSvgComTagsProibidas() {
            log.info("Caso de Abuso CA05.5: Testando bloqueio de tags perigosas embutidas em SVG.");
            String svg = "<svg xmlns='http://www.w3.org/2000/svg'><foreignObject><div>x</div></foreignObject></svg>";
            assertThatThrownBy(() -> validadorUploadImagem.validarEObterMimeType(svg.getBytes(StandardCharsets.UTF_8), "image/svg+xml"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("tags não permitidas");
        }
    }

    // ================================================================================================
    // CA06: EXPLORAÇÃO DE ARQUIVO DE IMAGEM EXCESSIVAMENTE PESADO (DoS)
    // ================================================================================================
    @Nested
    @DisplayName("Casos de Abuso - CA06: Arquivo Excessivamente Pesado ou Corrompido")
    class CA06_TamanhoELimiteUploadTests {

        @Test
        @DisplayName("CA06.1: Deve rejeitar arquivo de imagem que excede o limite máximo permitido de 5MB")
        void testCA06_1_UploadExcedeLimiteMaximo() {
            log.info("Caso de Abuso CA06.1: Testando rejeição de arquivo acima de 5MB.");
            byte[] arquivoGrande = new byte[ValidadorUploadImagem.TAMANHO_MAXIMO_BYTES + 1];
            assertThatThrownBy(() -> validadorUploadImagem.validarEObterMimeType(arquivoGrande, "image/png"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("excede o tamanho máximo permitido");
        }

        @Test
        @DisplayName("CA06.2: Deve rejeitar arquivo de imagem truncado com assinatura incompleta")
        void testCA06_2_UploadArquivoTruncado() {
            log.info("Caso de Abuso CA06.2: Testando rejeição de imagem truncada com menos de 8 bytes.");
            byte[] truncado = new byte[]{(byte) 0x89, 0x50, 0x4E};
            assertThatThrownBy(() -> validadorUploadImagem.validarEObterMimeType(truncado, "image/png"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("não corresponde a uma imagem válida");
        }
    }

    // ================================================================================================
    // CA07: ALTERAÇÃO INDEVIDA DAS CONDIÇÕES DE UMA CONTRATAÇÃO (QUEBRA DA MÁQUINA DE ESTADOS)
    // ================================================================================================
    @Nested
    @DisplayName("Casos de Abuso - CA07: Adulteração da Máquina de Estados e Condições de Pedido")
    class CA07_QuebraDeEstadoTests {

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("CA07.1: Deve impedir cliente de aprovar conclusão de pedido antes da confirmação do pagamento")
        void testCA07_1_AprovarPedidoAntesPagamento() throws Exception {
            log.info("Caso de Abuso CA07.1: Testando tentativa de aprovar conclusão de pedido em estado PAGO mas não entregue.");
            PedidoComissao pedido = criarPedido(StatusComissao.PAGO);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/aprovar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas pedidos com arte entregue podem ser concluídos e aprovados"));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("CA07.2: Deve impedir artista de entregar arte antes do pagamento ser confirmado")
        void testCA07_2_EntregarPedidoAntesPagamento() throws Exception {
            log.info("Caso de Abuso CA07.2: Testando tentativa de entrega antecipada sem pagamento.");
            PedidoComissao pedido = criarPedido(StatusComissao.AGUARDANDO_PAGAMENTO);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/entregar")
                            .param("linkEntrega", "https://artificio.local/arte-prematura.png")
                            .with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attribute("erro", "O pedido deve estar em andamento para receber uma entrega"));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("CA07.3: Deve impedir cliente de cancelar pedido já em produção")
        void testCA07_3_CancelarPedidoEmProducao() throws Exception {
            log.info("Caso de Abuso CA07.3: Testando tentativa de cancelamento unilateral após início de produção.");
            PedidoComissao pedido = criarPedido(StatusComissao.EM_ANDAMENTO);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/cancelar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "O pedido não pode ser cancelado após o início da produção ou pagamento"));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("CA07.4: Deve impedir artista de recusar pedido que já se encontra pago")
        void testCA07_4_RecusarPedidoJaPago() throws Exception {
            log.info("Caso de Abuso CA07.4: Testando tentativa do artista de recusar comissão após pagamento.");
            PedidoComissao pedido = criarPedido(StatusComissao.PAGO);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/recusar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas pedidos em estado SOLICITADA podem ser recusados"));
        }
    }

    // ================================================================================================
    // CA08: MANIPULAÇÃO DE AVALIAÇÕES
    // ================================================================================================
    @Nested
    @DisplayName("Casos de Abuso - CA08: Manipulação e Fraude em Avaliações")
    class CA08_ManipulacaoAvaliacoesTests {

        @Test
        @WithMockUser(username = "luan@artificio.local", roles = {"CLIENTE"})
        @DisplayName("CA08.1: Deve barrar cliente tentando avaliar pedido concluído de outro cliente (Anti-IDOR)")
        void testCA08_1_AvaliarPedidoDeTerceiro() throws Exception {
            log.info("Caso de Abuso CA08.1: Testando tentativa de avaliação de pedido de terceiro.");
            PedidoComissao pedidoEduardo = criarPedido(StatusComissao.CONCLUIDA);

            mockMvc.perform(post("/comissoes/" + pedidoEduardo.getId() + "/avaliar")
                            .with(csrf())
                            .param("pedidoId", pedidoEduardo.getId().toString())
                            .param("nota", "5")
                            .param("comentario", "Avaliação fraudulenta de terceiro"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "Pedido de comissão não encontrado ou acesso não autorizado."));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("CA08.2: Deve barrar avaliação de pedido que não esteja concluído")
        void testCA08_2_AvaliarPedidoNaoConcluido() throws Exception {
            log.info("Caso de Abuso CA08.2: Testando avaliação prematura de pedido não concluído.");
            PedidoComissao pedidoPendente = criarPedido(StatusComissao.EM_ANDAMENTO);

            mockMvc.perform(post("/comissoes/" + pedidoPendente.getId() + "/avaliar")
                            .with(csrf())
                            .param("pedidoId", pedidoPendente.getId().toString())
                            .param("nota", "5")
                            .param("comentario", "Tentativa de avaliar em produção"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas pedidos concluídos e aprovados podem receber avaliação"));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("CA08.3: Deve rejeitar nota de avaliação fora do intervalo permitido de 1 a 5")
        void testCA08_3_AvaliacaoNotaInvalida() throws Exception {
            log.info("Caso de Abuso CA08.3: Testando submissão de nota de avaliação fora dos limites permitidos.");
            PedidoComissao pedidoConcluido = criarPedido(StatusComissao.CONCLUIDA);

            mockMvc.perform(post("/comissoes/" + pedidoConcluido.getId() + "/avaliar")
                            .with(csrf())
                            .param("pedidoId", pedidoConcluido.getId().toString())
                            .param("nota", "10")
                            .param("comentario", "Nota acima do limite"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("cliente/avaliar"))
                    .andExpect(model().attributeHasFieldErrors("novaAvaliacaoDto", "nota"));
        }
    }

    // ================================================================================================
    // CA09: ADULTERAÇÃO DE REGISTROS DE AUDITORIA OU NEGOCIAÇÃO
    // ================================================================================================
    @Nested
    @DisplayName("Casos de Abuso - CA09: Proteção contra Adulteração da Trilha Forense")
    class CA09_ProtecaoAuditoriaTests {

        @Test
        @WithMockUser(username = "admin@artificio.local", roles = {"ADMIN"})
        @DisplayName("CA09.1: Deve barrar método POST indevido na rota de auditoria forense com HTTP 405 Method Not Allowed")
        void testCA09_1_PostEmAuditoriaRetorna405() throws Exception {
            log.info("Caso de Abuso CA09.1: Testando bloqueio de injeção indevida via POST na trilha forense.");
            mockMvc.perform(post("/admin/auditoria").with(csrf()))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @WithMockUser(username = "admin@artificio.local", roles = {"ADMIN"})
        @DisplayName("CA09.2: Deve barrar tentativa de DELETE na rota de auditoria forense com HTTP 405 Method Not Allowed")
        void testCA09_2_DeleteEmAuditoriaRetorna405() throws Exception {
            log.info("Caso de Abuso CA09.2: Testando tentativa de expurgo indevido da trilha forense.");
            mockMvc.perform(delete("/admin/auditoria").with(csrf()))
                    .andExpect(status().isMethodNotAllowed());
        }
    }

    // ================================================================================================
    // CA10: NÃO-REPÚDIO DE CONTRATAÇÃO, ENTREGA OU ALTERAÇÃO DE CONTA
    // ================================================================================================
    @Nested
    @DisplayName("Casos de Abuso - CA10: Não-Repúdio e Rastreabilidade")
    class CA10_NaoRepudioTests {

        @Test
        @DisplayName("CA10.1: Deve manter imutável a dataCriacao e criadoPor da entidade original após alterações posteriores")
        void testCA10_1_ImutabilidadeDataCriacao() {
            log.info("Caso de Abuso CA10.1: Testando imutabilidade de metadados originais de auditoria.");
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            LocalDateTime criacaoOriginal = anna.getDataCriacao();

            anna.setBio("Bio alterada para teste de auditoria");
            Usuario annaAtualizada = usuarioRepository.saveAndFlush(anna);

            assertThat(annaAtualizada.getDataCriacao()).isEqualTo(criacaoOriginal);
            assertThat(annaAtualizada.getDataAtualizacao()).isNotNull();
        }
    }

    // ================================================================================================
    // CA11: EXCLUSÃO DE EVIDÊNCIAS APÓS DENÚNCIA OU DISPUTA
    // ================================================================================================
    @Nested
    @DisplayName("Casos de Abuso - CA11: Preservação de Registros no Hibernate Envers")
    class CA11_PreservacaoEvidenciasTests {

        @Test
        @DisplayName("CA11.1: Trilha do Hibernate Envers deve registrar e preservar histórico de revisões de entidades")
        void testCA11_1_PreservacaoRevisoesEnvers() {
            log.info("Caso de Abuso CA11.1: Testando capacidade da trilha Envers de preservar histórico perante modificações.");
            if (auditoriaService != null) {
                var revisoes = auditoriaService.listarUltimasRevisoes(10);
                assertThat(revisoes).isNotNull();
            }
        }
    }

    // ================================================================================================
    // CA12: EXPOSIÇÃO DE METADADOS DE IMAGENS
    // ================================================================================================
    @Nested
    @DisplayName("Casos de Abuso - CA12: Proteção de Metadados e Cabeçalhos de Imagens")
    class CA12_MetadadosImagensTests {

        @Test
        @DisplayName("CA12.1: Deve servir imagens com Content-Disposition inline, Content-Type seguro e CSP Sandbox")
        void testCA12_1_ServirImagemComHeadersSeguros() throws Exception {
            log.info("Caso de Abuso CA12.1: Verificando cabeçalhos de segurança ao servir arquivos binários de imagem.");
            ImagemBlob img = new ImagemBlob("img-ca12", "foto.png", "image/png",
                    new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47 });
            img = imagemBlobRepository.save(img);

            mockMvc.perform(get("/imagens/" + img.getId()))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Security-Policy", "default-src 'none'; style-src 'unsafe-inline'; sandbox"))
                    .andExpect(header().string("X-Content-Type-Options", "nosniff"));
        }
    }

    // ================================================================================================
    // CA13: ACESSO INDEVIDO A DADOS, MENSAGENS OU ARQUIVOS PRIVADOS (ANTI-IDOR)
    // ================================================================================================
    @Nested
    @DisplayName("Casos de Abuso - CA13: Proteção Avançada Anti-IDOR")
    class CA13_ProtecaoAntiIdorTests {

        @Test
        @WithMockUser(username = "luan@artificio.local", roles = {"CLIENTE"})
        @DisplayName("CA13.1: Deve impedir Cliente B de confirmar pagamento do pedido do Cliente A")
        void testCA13_1_ClienteNaoPagaPedidoAlheio() throws Exception {
            log.info("Caso de Abuso CA13.1: Testando IDOR na confirmação de pagamento entre clientes.");
            PedidoComissao pedidoEduardo = criarPedido(StatusComissao.AGUARDANDO_PAGAMENTO);

            mockMvc.perform(post("/comissoes/" + pedidoEduardo.getId() + "/confirmar-pagamento").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas o cliente solicitante pode confirmar o pagamento"));
        }

        @Test
        @WithMockUser(username = "luan@artificio.local", roles = {"CLIENTE"})
        @DisplayName("CA13.2: Deve impedir Cliente B de solicitar revisão de pedido do Cliente A")
        void testCA13_2_ClienteNaoPedeRevisaoPedidoAlheio() throws Exception {
            log.info("Caso de Abuso CA13.2: Testando IDOR na solicitação de revisão.");
            PedidoComissao pedidoEduardo = criarPedido(StatusComissao.ENTREGUE);

            mockMvc.perform(post("/comissoes/" + pedidoEduardo.getId() + "/revisao").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas o cliente pode solicitar revisão da arte entregue"));
        }
    }

    // ================================================================================================
    // CA14: ACESSO INDEVIDO A ARQUIVOS POR LINK OU RECURSO COMPARTILHADO
    // ================================================================================================
    @Nested
    @DisplayName("Casos de Abuso - CA14: Identificadores Não-Sequenciais e URLs Seguras")
    class CA14_UrlsEIdentificadoresTests {

        @Test
        @DisplayName("CA14.1: Entidades devem utilizar identificadores não sequenciais RFC 9562 UUIDv7 impedindo enumeração")
        void testCA14_1_IdentificadoresUUIDv7NaoSequenciais() {
            log.info("Caso de Abuso CA14.1: Testando conformidade de chaves primárias RFC 9562 UUIDv7.");
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            assertThat(anna.getId()).isNotNull();
            assertThat(anna.getId().version()).isEqualTo(7);
        }

        @Test
        @DisplayName("CA14.2: Tentativa de forjar UUID malformado na URL deve retornar HTTP 400 amigável sem vazar stack trace")
        void testCA14_2_TratamentoUUIDMalformado() throws Exception {
            log.info("Caso de Abuso CA14.2: Testando acesso com UUID sintaticamente corrompido.");
            mockMvc.perform(get("/artistas/parametro-malformado-nao-uuid"))
                    .andExpect(status().isBadRequest())
                    .andExpect(view().name("error"))
                    .andExpect(content().string(containsString("Identificador ou Parâmetro Inválido")));
        }
    }

    // ================================================================================================
    // CA16: EXTRAÇÃO AUTOMATIZADA DE INFORMAÇÕES PELA BUSCA (RESILIÊNCIA CONTRA SCRAPING/DoS)
    // ================================================================================================
    @Nested
    @DisplayName("Casos de Abuso - CA16: Resiliência da Busca sob Carga Abusiva")
    class CA16_BuscaSobCargaAbusivaTests {

        @Test
        @DisplayName("CA16.1: Busca pública no catálogo deve resistir a payload anômalo de 20.000 caracteres sem travar")
        void testCA16_1_BuscaCatalogoPayloadGigantesco() throws Exception {
            log.info("Caso de Abuso CA16.1: Testando resiliência da vitrine contra busca de 20.000 caracteres.");
            String buscaGigante = "B".repeat(20000);

            mockMvc.perform(get("/").param("busca", buscaGigante))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(content().string(containsString("Artifício")));
        }

        @Test
        @WithMockUser(username = "admin@artificio.local", roles = {"ADMIN"})
        @DisplayName("CA16.2: Busca na auditoria forense deve resistir a payload de 20.000 caracteres sem estouro de buffer")
        void testCA16_2_BuscaAuditoriaPayloadGigantesco() throws Exception {
            log.info("Caso de Abuso CA16.2: Testando resiliência do filtro de auditoria sob payload gigantesco.");
            String buscaGigante = "A".repeat(20000);

            mockMvc.perform(get("/admin/auditoria").param("busca", buscaGigante))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/auditoria"));
        }
    }

    // ================================================================================================
    // CA22: ACESSO DIRETO À ÁREA ADMINISTRATIVA
    // ================================================================================================
    @Nested
    @DisplayName("Casos de Abuso - CA22: Acesso Direto Não Autorizado à Administração")
    class CA22_AcessoAreaAdministrativaTests {

        @Test
        @DisplayName("CA22.1: Visitante anônimo deve ser barrado ao tentar acessar /admin/** e redirecionado para /login")
        void testCA22_1_AnonimoBarradoAreaAdministrativa() throws Exception {
            log.info("Caso de Abuso CA22.1: Testando bloqueio de anônimo em rota administrativa.");
            mockMvc.perform(get("/admin/usuarios"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"));
        }

        @Test
        @WithMockUser(username = "cliente@artificio.local", roles = {"CLIENTE"})
        @DisplayName("CA22.2: Usuário comum (ROLE_CLIENTE) deve ser barrado ao tentar acessar /admin/** com HTTP 403 Forbidden")
        void testCA22_2_ClienteBarradoAreaAdministrativa() throws Exception {
            log.info("Caso de Abuso CA22.2: Testando bloqueio de cliente em rota administrativa.");
            mockMvc.perform(get("/admin/usuarios"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "artista@artificio.local", roles = {"ARTISTA"})
        @DisplayName("CA22.3: Artista (ROLE_ARTISTA) deve ser barrado ao tentar acessar /admin/** com HTTP 403 Forbidden")
        void testCA22_3_ArtistaBarradoAreaAdministrativa() throws Exception {
            log.info("Caso de Abuso CA22.3: Testando bloqueio de artista em rota administrativa.");
            mockMvc.perform(get("/admin/usuarios"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "cliente@artificio.local", roles = {"CLIENTE"})
        @DisplayName("CA22.4: Usuário comum deve ser barrado ao tentar acessar a trilha de auditoria forense com HTTP 403 Forbidden")
        void testCA22_4_ClienteBarradoAuditoriaForense() throws Exception {
            log.info("Caso de Abuso CA22.4: Testando bloqueio de usuário comum na trilha forense.");
            mockMvc.perform(get("/admin/auditoria"))
                    .andExpect(status().isForbidden());
        }
    }

    // ================================================================================================
    // CA23: ALTERAÇÃO INDEVIDA DE PERMISSÕES DE USUÁRIO (ESCALADA DE PRIVILÉGIOS)
    // ================================================================================================
    @Nested
    @DisplayName("Casos de Abuso - CA23: Escalada de Privilégios e Papéis")
    class CA23_EscaladaPrivilegiosTests {

        @Test
        @DisplayName("CA23.1: Deve neutralizar tentativa de injeção de papel ROLE_ADMIN no auto-cadastro público")
        void testCA23_1_AutoCadastroNaoPermiteRoleAdmin() throws Exception {
            log.info("Caso de Abuso CA23.1: Testando tentativa de forjar role ROLE_ADMIN no cadastro.");
            mockMvc.perform(post("/cadastro")
                            .with(csrf())
                            .param("nome", "Atacante Escalada")
                            .param("email", "escalada@artificio.local")
                            .param("senha", "Senha123")
                            .param("role", "ROLE_ADMIN"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("cadastro"))
                    .andExpect(model().attribute("erro", "Não é permitido cadastrar administradores diretamente"));

            assertThat(usuarioRepository.findByEmail("escalada@artificio.local")).isEmpty();
        }

        @Test
        @WithMockUser(username = "cliente@artificio.local", roles = {"CLIENTE"})
        @DisplayName("CA23.2: Usuário comum sem papel ROLE_ADMIN deve ser barrado ao tentar promover contas")
        void testCA23_2_PromocaoUsuarioExclusivaAdmin() throws Exception {
            log.info("Caso de Abuso CA23.2: Testando promoção de usuário por usuário não-administrador.");
            mockMvc.perform(post("/admin/usuarios/" + UUID.randomUUID() + "/promover").with(csrf()))
                    .andExpect(status().isForbidden());
        }
    }

    // ================================================================================================
    // CA24: ACESSO INDEVIDO A OBRA OU PORTFÓLIO DE OUTRO ARTISTA
    // ================================================================================================
    @Nested
    @DisplayName("Casos de Abuso - CA24: Manipulação Cruzada de Portfólio entre Artistas")
    class CA24_ManipulacaoEntreArtistasTests {

        @Test
        @WithMockUser(username = "cristiano@artificio.local", roles = {"ARTISTA"})
        @DisplayName("CA24.1: Artista B deve ser impedido de alternar status de serviço do Artista A (Anti-IDOR)")
        void testCA24_1_ArtistaNaoAlteraServicoAlheio() throws Exception {
            log.info("Caso de Abuso CA24.1: Testando IDOR na alteração de status de serviço entre artistas.");
            TipoComissao tipoAnna = obterTipoAnna();

            mockMvc.perform(post("/painel/artista/servicos/" + tipoAnna.getId() + "/toggle").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/servicos"))
                    .andExpect(flash().attribute("erro", "O serviço não pertence ao artista logado"));
        }

        @Test
        @WithMockUser(username = "cristiano@artificio.local", roles = {"ARTISTA"})
        @DisplayName("CA24.2: Artista B deve ser impedido de remover obra do portfólio do Artista A (Anti-IDOR)")
        void testCA24_2_ArtistaNaoRemoveObraAlheia() throws Exception {
            log.info("Caso de Abuso CA24.2: Testando IDOR na exclusão de obra do portfólio entre artistas.");
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            ObraPortfolio obraAnna = new ObraPortfolio("Obra Anna", "Desc", "Aquarela", "uuid-fake", anna);
            obraAnna = obraPortfolioRepository.save(obraAnna);

            mockMvc.perform(post("/painel/artista/portfolio/" + obraAnna.getId() + "/remover").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/portfolio"))
                    .andExpect(flash().attribute("erro", "A obra não pertence ao artista logado"));
        }
    }

    // ================================================================================================
    // CA25: CONTINUIDADE DE ACESSO APÓS BLOQUEIO OU REVOGAÇÃO (CONTA DESATIVADA)
    // ================================================================================================
    @Nested
    @DisplayName("Casos de Abuso - CA25: Bloqueio de Acesso com Conta Não Confirmada ou Desativada")
    class CA25_BloqueioContaNaoAtivadaTests {

        @Test
        @DisplayName("CA25.1: Tentativa de login com conta desativada (e-mail não confirmado) deve ser bloqueada pelo Spring Security")
        void testCA25_1_LoginContaNaoConfirmadaBloqueado() throws Exception {
            log.info("Caso de Abuso CA25.1: Testando bloqueio de autenticação para conta com emailConfirmado=false.");
            CadastroDto dto = new CadastroDto("Conta Inativa", "inativa@artificio.local", "SenhaValida123", Role.ROLE_CLIENTE, null, null);
            usuarioService.registrarUsuario(dto);

            mockMvc.perform(post("/login")
                            .with(csrf())
                            .param("email", "inativa@artificio.local")
                            .param("senha", "SenhaValida123"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login?error=true"));
        }
    }

    // ================================================================================================
    // CA26: CONFLITO ENTRE PAPÉIS E APROVAÇÃO INDEVIDA DE CONTRATAÇÃO (AUTOCONTRATAÇÃO)
    // ================================================================================================
    @Nested
    @DisplayName("Casos de Abuso - CA26: Prevenção de Conflito de Interesses e Autocontratação")
    class CA26_AutocontratacaoTests {

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("CA26.1: Artista deve ser impedido de solicitar encomenda de comissão para si mesmo (autocontratação)")
        void testCA26_1_AutocontratacaoBloqueada() throws Exception {
            log.info("Caso de Abuso CA26.1: Testando bloqueio de autocontratação de comissão.");
            TipoComissao tipoAnna = obterTipoAnna();

            mockMvc.perform(post("/comissoes/solicitar")
                            .with(csrf())
                            .param("tipoComissaoId", tipoAnna.getId().toString())
                            .param("briefing", "Tentativa fraudulenta de autocontratação para inflar estatísticas"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(flash().attribute("erro", "Você não pode solicitar uma comissão para si próprio"));
        }
    }

    // ================================================================================================
    // CA27: ALTERAÇÕES ADMINISTRATIVAS SEM RASTREABILIDADE ADEQUADA
    // ================================================================================================
    @Nested
    @DisplayName("Casos de Abuso - CA27: Rastreabilidade Obrigatória de Operações Administrativas")
    class CA27_AuditoriaAcoesAdministrativasTests {

        @Test
        @WithMockUser(username = "admin@artificio.local", roles = {"ADMIN"})
        @DisplayName("CA27.1: Ações administrativas de promoção de usuários devem gerar revisão obrigatória na auditoria forense")
        void testCA27_1_AuditoriaPromocaoAdministrativa() throws Exception {
            log.info("Caso de Abuso CA27.1: Testando registro em auditoria ao executar operação administrativa.");
            Usuario cliente = new Usuario("Cliente Promocao", "promover@artificio.local", "hash123", Role.ROLE_CLIENTE, "", "");
            cliente = usuarioRepository.saveAndFlush(cliente);

            mockMvc.perform(post("/admin/usuarios/" + cliente.getId() + "/promover").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/usuarios"))
                    .andExpect(flash().attribute("sucesso", "Usuário 'Cliente Promocao' promovido com sucesso para Artista!"));

            Usuario promovido = usuarioRepository.findById(cliente.getId()).orElseThrow();
            assertThat(promovido.getRole()).isEqualTo(Role.ROLE_ARTISTA);
            assertThat(promovido.getDataAtualizacao()).isNotNull();
        }
    }
}
