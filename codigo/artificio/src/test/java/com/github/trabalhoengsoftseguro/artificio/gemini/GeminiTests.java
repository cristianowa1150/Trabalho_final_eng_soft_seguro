package com.github.trabalhoengsoftseguro.artificio.gemini;

import com.github.trabalhoengsoftseguro.artificio.artista.TipoComissao;
import com.github.trabalhoengsoftseguro.artificio.artista.TipoComissaoRepository;
import com.github.trabalhoengsoftseguro.artificio.comissao.PedidoComissao;
import com.github.trabalhoengsoftseguro.artificio.comissao.PedidoComissaoRepository;
import com.github.trabalhoengsoftseguro.artificio.comissao.StatusComissao;
import com.github.trabalhoengsoftseguro.artificio.imagem.ImagemBlob;
import com.github.trabalhoengsoftseguro.artificio.imagem.ImagemBlobRepository;
import com.github.trabalhoengsoftseguro.artificio.usuario.Role;
import com.github.trabalhoengsoftseguro.artificio.usuario.Usuario;
import com.github.trabalhoengsoftseguro.artificio.usuario.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

import com.github.trabalhoengsoftseguro.artificio.artista.ObraPortfolio;
import com.github.trabalhoengsoftseguro.artificio.artista.ObraPortfolioRepository;
import com.github.trabalhoengsoftseguro.artificio.imagem.ValidadorUploadImagem;
import com.github.trabalhoengsoftseguro.artificio.usuario.TokenAtivacao;
import com.github.trabalhoengsoftseguro.artificio.usuario.TokenAtivacaoRepository;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * ====================================================================================================
 * SUÍTE DE TESTES ANÔMALOS E DE RESILIÊNCIA - ETAPA 3
 * ====================================================================================================
 * Estes testes foram elaborados pelo Gemini para checar sistematicamente as
 * possibilidades de espúrios, comportamentos de borda e situações bizantinas no sistema Artifício.
 * ====================================================================================================
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class GeminiTests {

    private static final Logger log = LoggerFactory.getLogger(GeminiTests.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private TipoComissaoRepository tipoComissaoRepository;

    @Autowired
    private PedidoComissaoRepository pedidoComissaoRepository;

    @Autowired
    private ImagemBlobRepository imagemBlobRepository;

    @Autowired
    private ObraPortfolioRepository obraPortfolioRepository;

    @Autowired
    private TokenAtivacaoRepository tokenAtivacaoRepository;

    @Autowired
    private ValidadorUploadImagem validadorUploadImagem;

    // ------------------------------------------------------------------------------------------------
    // 1. CATÁLOGO E BUSCA ANÔMALA
    // ------------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Testes Gemini: Catálogo e Busca sob Entradas Extremas e Espúrias")
    class CatalogoEBuscaAnomalaTests {

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve suportar busca com string gigantesca (20.000 caracteres) sem estouro de buffer ou erro 500")
        void testBuscaComStringGigantesca() throws Exception {
            log.info("Elaborado pelo Gemini: Executando teste de robustez para busca com string gigantesca.");
            String buscaGigante = "A".repeat(20000);

            mockMvc.perform(get("/").param("busca", buscaGigante))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(content().string(containsString("Nenhum artista encontrado")));
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve suportar busca com caracteres de controle, nulos e quebras de linha")
        void testBuscaComCaracteresDeControleENulos() throws Exception {
            log.info("Elaborado pelo Gemini: Executando teste para busca com caracteres nulos e de controle.");
            String buscaAnomala = "\u0000\u0001\r\n\t   ";

            mockMvc.perform(get("/").param("busca", buscaAnomala))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"));
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve suportar busca com Unicode exótico, emojis múltiplos e homóglifos")
        void testBuscaComUnicodeEEmojis() throws Exception {
            log.info("Elaborado pelo Gemini: Executando teste para busca com caracteres Unicode compostos e emojis.");
            String buscaExotica = "🎨🖌️ 𝕬𝖗𝖙𝖊 𝔖𝔢𝔤𝖚𝖗𝖆 👩‍🎨 Привет мир ﷽";

            mockMvc.perform(get("/").param("busca", buscaExotica))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"));
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve tratar payloads de teste de injeção na busca parametrizada sem falha de sintaxe SQL")
        void testBuscaComPayloadsDeInjecaoSintatica() throws Exception {
            log.info(
                    "Elaborado pelo Gemini: Testando resiliência do repositório JPA contra payloads de injeção em parâmetros de busca.");
            String payloadInjecao = "' OR 1=1 UNION SELECT null, null, null --";

            mockMvc.perform(get("/").param("busca", payloadInjecao))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"));
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve tratar categoria inexistente e espúria retornando catálogo vazio de forma elegante")
        void testFiltroCategoriaInexistente() throws Exception {
            log.info("Elaborado pelo Gemini: Testando filtragem com categoria inexistente.");
            mockMvc.perform(get("/").param("categoria", "CategoriaInexistenteEspuriaXYZ999"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("index"))
                    .andExpect(content().string(containsString("Nenhum artista encontrado")));
        }
    }

    // ------------------------------------------------------------------------------------------------
    // 2. IDENTIFICADORES UUID E ROTAS MALFORMADAS
    // ------------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Testes Gemini: Resiliência Defensiva com UUIDs Malformados e Inexistentes")
    class IdentificadoresUUIDEMalformadosTests {

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve retornar HTTP 400 amigável com view de erro para URL com UUID malformado em perfil público")
        void testPerfilArtistaComUUIDMalformado() throws Exception {
            log.info("Elaborado pelo Gemini: Testando rota pública com UUID sintaticamente corrompido.");
            mockMvc.perform(get("/artistas/isto-definitivamente-nao-e-um-uuid"))
                    .andExpect(status().isBadRequest())
                    .andExpect(view().name("error"))
                    .andExpect(model().attributeExists("status"))
                    .andExpect(model().attributeExists("mensagem"))
                    .andExpect(content().string(containsString("Identificador ou Parâmetro Inválido")));
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve retornar HTTP 404 amigável com view de erro para UUID válido porém inexistente no banco")
        void testPerfilArtistaComUUIDInexistente() throws Exception {
            log.info("Elaborado pelo Gemini: Testando rota pública com UUID inexistente.");
            UUID uuidInexistente = UUID.fromString("018f0000-0000-7000-8000-000000000000");

            mockMvc.perform(get("/artistas/" + uuidInexistente))
                    .andExpect(status().isNotFound())
                    .andExpect(view().name("error"))
                    .andExpect(model().attribute("status", 404))
                    .andExpect(content().string(containsString("Recurso Não Encontrado")));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = { "CLIENTE" })
        @DisplayName("Elaborado pelo Gemini: Deve capturar formulário de contratação com modalidade malformada e exibir tela defensiva")
        void testSolicitacaoComissaoComUUIDTipoMalformado() throws Exception {
            log.info("Elaborado pelo Gemini: Testando solicitação de comissão com UUID malformado na rota.");
            mockMvc.perform(get("/comissoes/solicitar/uuid-corrompido-xyz"))
                    .andExpect(status().isBadRequest())
                    .andExpect(view().name("error"))
                    .andExpect(model().attribute("status", 400));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = { "CLIENTE" })
        @DisplayName("Elaborado pelo Gemini: Deve tratar formulário de contratação com modalidade inexistente sem quebrar em HTTP 500")
        void testSolicitacaoComissaoComUUIDTipoInexistente() throws Exception {
            log.info("Elaborado pelo Gemini: Testando solicitação de comissão com UUID inexistente.");
            UUID uuidInexistente = UUID.fromString("018f9999-9999-7999-8999-999999999999");

            mockMvc.perform(get("/comissoes/solicitar/" + uuidInexistente))
                    .andExpect(status().isNotFound())
                    .andExpect(view().name("error"))
                    .andExpect(model().attribute("status", 404));
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve retornar 404 para busca de imagem com identificador inexistente")
        void testExibirImagemComIdInexistente() throws Exception {
            log.info("Elaborado pelo Gemini: Testando busca de imagem com identificador inexistente.");
            mockMvc.perform(get("/imagens/imagem-inexistente-12345"))
                    .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(username = "admin@artificio.local", roles = { "ADMIN" })
        @DisplayName("Elaborado pelo Gemini: Deve tratar promoção de usuário com UUID inexistente com mensagem flash sem estourar 500")
        void testPromoverUsuarioComUUIDInexistente() throws Exception {
            log.info("Elaborado pelo Gemini: Testando endpoint de promoção administrativa com usuário inexistente.");
            UUID uuidInexistente = UUID.randomUUID();

            mockMvc.perform(post("/admin/usuarios/" + uuidInexistente + "/promover").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin/usuarios"))
                    .andExpect(flash().attributeExists("erro"));
        }
    }

    // ------------------------------------------------------------------------------------------------
    // 3. CADASTRO E AUTENTICAÇÃO COM VALORES DE BORDA
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
    // 4. ARTISTA E SERVIÇOS: PREÇOS E PRAZOS NEGATIVOS E ASTRONÔMICOS
    // ------------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Testes Gemini: Serviços com Preços Negativos, Prazos Nulos e Valores Extremos")
    class ArtistaEServicosValoresExtremosTests {

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = { "ARTISTA" })
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar cadastro de modalidade com preço base negativo (-150.00)")
        void testCadastroServicoComPrecoNegativo() throws Exception {
            log.info("Elaborado pelo Gemini: Testando tentativa de cadastro de serviço com preço negativo.");
            mockMvc.perform(post("/painel/artista/servicos/novo")
                    .with(csrf())
                    .param("titulo", "Ilustração Promocional Negativa")
                    .param("descricao", "Tentativa de colocar preço negativo")
                    .param("categoria", "Aquarela")
                    .param("precoBase", "-150.00")
                    .param("prazoEstimadoDias", "7"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("artista/servicos"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrors("novoTipoDto", "precoBase"));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = { "ARTISTA" })
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar cadastro de modalidade com preço zero (0.00)")
        void testCadastroServicoComPrecoZero() throws Exception {
            log.info("Elaborado pelo Gemini: Testando tentativa de cadastro de serviço com preço zero.");
            mockMvc.perform(post("/painel/artista/servicos/novo")
                    .with(csrf())
                    .param("titulo", "Arte Gratuita Não Permitida")
                    .param("descricao", "Tentativa com zero centavos")
                    .param("categoria", "Aquarela")
                    .param("precoBase", "0.00")
                    .param("prazoEstimadoDias", "7"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("artista/servicos"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrors("novoTipoDto", "precoBase"));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = { "ARTISTA" })
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar preço astronômico excedendo o limite @DecimalMax (R$ 100.000.000,00)")
        void testCadastroServicoComPrecoAstronomico() throws Exception {
            log.info("Elaborado pelo Gemini: Testando tentativa com preço astronômico.");
            mockMvc.perform(post("/painel/artista/servicos/novo")
                    .with(csrf())
                    .param("titulo", "Arte Hiper Cara")
                    .param("descricao", "Tentativa com valor de 100 milhões")
                    .param("categoria", "Aquarela")
                    .param("precoBase", "100000000.00")
                    .param("prazoEstimadoDias", "7"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("artista/servicos"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrors("novoTipoDto", "precoBase"));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = { "ARTISTA" })
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar prazo de entrega negativo ou zero dias")
        void testCadastroServicoComPrazoNegativoOuZero() throws Exception {
            log.info("Elaborado pelo Gemini: Testando prazo de entrega de 0 e negativo.");
            mockMvc.perform(post("/painel/artista/servicos/novo")
                    .with(csrf())
                    .param("titulo", "Prazo Imediato Impossível")
                    .param("descricao", "Tentativa de prazo zero")
                    .param("categoria", "Aquarela")
                    .param("precoBase", "100.00")
                    .param("prazoEstimadoDias", "0"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("artista/servicos"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrors("novoTipoDto", "prazoEstimadoDias"));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = { "ARTISTA" })
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar prazo de entrega absurdo de 1000 dias (máximo é 365)")
        void testCadastroServicoComPrazoAbsurdo() throws Exception {
            log.info("Elaborado pelo Gemini: Testando prazo absurdo de 1000 dias.");
            mockMvc.perform(post("/painel/artista/servicos/novo")
                    .with(csrf())
                    .param("titulo", "Prazo de Três Anos")
                    .param("descricao", "Tentativa de prazo muito longo")
                    .param("categoria", "Aquarela")
                    .param("precoBase", "100.00")
                    .param("prazoEstimadoDias", "1000"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("artista/servicos"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrors("novoTipoDto", "prazoEstimadoDias"));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = { "ARTISTA" })
        @DisplayName("Elaborado pelo Gemini: Deve tratar tentativa de alternar status (toggle) de serviço inexistente")
        void testToggleServicoInexistente() throws Exception {
            log.info("Elaborado pelo Gemini: Testando alternar status de serviço com UUID inexistente.");
            UUID uuidInexistente = UUID.randomUUID();

            mockMvc.perform(post("/painel/artista/servicos/" + uuidInexistente + "/toggle").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/servicos"))
                    .andExpect(flash().attributeExists("erro"));
        }
    }

    // ------------------------------------------------------------------------------------------------
    // 5. UPLOADS CORROMPIDOS E VETORES SVG MALICIOSOS
    // ------------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Testes Gemini: Uploads Corrompidos, Magic Bytes e Proteção SVG")
    class UploadDeImagensEPortfoliosAnomalosTests {

        @Test
        @WithMockUser(username = "cristiano@artificio.local", roles = { "ARTISTA" })
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar upload de obra com arquivo vazio (0 bytes)")
        void testUploadObraArquivoVazio() throws Exception {
            log.info("Elaborado pelo Gemini: Testando upload de arquivo vazio.");
            MockMultipartFile arquivoVazio = new MockMultipartFile(
                    "arquivo", "vazio.png", "image/png", new byte[0]);

            mockMvc.perform(multipart("/painel/artista/portfolio/novo")
                    .file(arquivoVazio)
                    .with(csrf())
                    .param("titulo", "Obra Fantasma Vazia")
                    .param("descricao", "Sem arquivo anexado")
                    .param("categoria", "Pixel Art"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("artista/portfolio"))
                    .andExpect(model().hasErrors());
        }

        @Test
        @WithMockUser(username = "cristiano@artificio.local", roles = { "ARTISTA" })
        @DisplayName("Elaborado pelo Gemini: Deve barrar upload de arquivo de texto disfarçado com extensão .png (falsificação de assinatura mágica)")
        void testUploadObraComAssinaturaMagicaInvalida() throws Exception {
            log.info("Elaborado pelo Gemini: Testando falsificação de MIME e magic bytes de imagem.");
            byte[] bytesTextoFalso = "ISTO EH UM ARQUIVO DE TEXTO E NAO UM PNG".getBytes(StandardCharsets.UTF_8);

            MockMultipartFile arquivoDisfarcado = new MockMultipartFile(
                    "arquivo", "virus.png", "image/png", bytesTextoFalso);

            mockMvc.perform(multipart("/painel/artista/portfolio/novo")
                    .file(arquivoDisfarcado)
                    .with(csrf())
                    .param("titulo", "PNG Falso")
                    .param("descricao", "Tentativa de burla de tipo de arquivo")
                    .param("categoria", "Pixel Art"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/portfolio"))
                    .andExpect(flash().attributeExists("erro"));
        }

        @Test
        @WithMockUser(username = "cristiano@artificio.local", roles = { "ARTISTA" })
        @DisplayName("Elaborado pelo Gemini: Deve barrar upload de vetor SVG contendo script ativo (Stored XSS)")
        void testUploadSvgComScriptMalicioso() throws Exception {
            log.info("Elaborado pelo Gemini: Testando SVG com injeção de script embutido.");
            String svgMalicioso = """
                    <svg xmlns="http://www.w3.org/2000/svg" width="100" height="100">
                        <circle cx="50" cy="50" r="40" fill="red" />
                        <script>alert('Ataque XSS bloqueado!');</script>
                    </svg>
                    """;

            MockMultipartFile svgScript = new MockMultipartFile(
                    "arquivo", "arte.svg", "image/svg+xml", svgMalicioso.getBytes(StandardCharsets.UTF_8));

            mockMvc.perform(multipart("/painel/artista/portfolio/novo")
                    .file(svgScript)
                    .with(csrf())
                    .param("titulo", "SVG com Script")
                    .param("descricao", "Tentativa de injeção de tag script no SVG")
                    .param("categoria", "Pixel Art"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/portfolio"))
                    .andExpect(flash().attributeExists("erro"));
        }

        @Test
        @WithMockUser(username = "cristiano@artificio.local", roles = { "ARTISTA" })
        @DisplayName("Elaborado pelo Gemini: Deve barrar upload de vetor SVG contendo manipuladores de eventos JavaScript (onload=)")
        void testUploadSvgComEventosJavaScript() throws Exception {
            log.info("Elaborado pelo Gemini: Testando SVG com atributos de evento JavaScript.");
            String svgComEvento = """
                    <svg xmlns="http://www.w3.org/2000/svg" width="100" height="100" onload="fetch('/admin')">
                        <rect width="100" height="100" fill="blue" />
                    </svg>
                    """;

            MockMultipartFile svgEvento = new MockMultipartFile(
                    "arquivo", "evento.svg", "image/svg+xml", svgComEvento.getBytes(StandardCharsets.UTF_8));

            mockMvc.perform(multipart("/painel/artista/portfolio/novo")
                    .file(svgEvento)
                    .with(csrf())
                    .param("titulo", "SVG com OnLoad")
                    .param("descricao", "Tentativa com atributo onload")
                    .param("categoria", "Pixel Art"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/portfolio"))
                    .andExpect(flash().attributeExists("erro"));
        }

        @Test
        @WithMockUser(username = "cristiano@artificio.local", roles = { "ARTISTA" })
        @DisplayName("Elaborado pelo Gemini: Deve tratar remoção de obra com UUID inexistente sem estourar 500")
        void testRemoverObraComUUIDInexistente() throws Exception {
            log.info("Elaborado pelo Gemini: Testando remoção de obra inexistente.");
            UUID uuidInexistente = UUID.randomUUID();

            mockMvc.perform(post("/painel/artista/portfolio/" + uuidInexistente + "/remover").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/portfolio"))
                    .andExpect(flash().attributeExists("erro"));
        }
    }

    // ------------------------------------------------------------------------------------------------
    // 6. COMISSÕES E TRANSIÇÕES DE ESTADO DA MÁQUINA DE PEDIDOS
    // ------------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Testes Gemini: Ciclo de Vida de Comissões, Quebra de Estado e Avaliações")
    class ComissoesETransicoesDeEstadoTests {

        private TipoComissao obterTipoAnna() {
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            return tipoComissaoRepository.findByArtistaIdAndAtivoTrue(anna.getId()).get(0);
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = { "CLIENTE" })
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar solicitação com briefing excessivamente curto (menos de 10 caracteres)")
        void testSolicitacaoComBriefingCurto() throws Exception {
            log.info("Elaborado pelo Gemini: Testando rejeição de briefing muito curto.");
            TipoComissao tipo = obterTipoAnna();

            mockMvc.perform(post("/comissoes/solicitar")
                    .with(csrf())
                    .param("tipoComissaoId", tipo.getId().toString())
                    .param("briefing", "Oi"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("cliente/solicitar"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrors("solicitacaoDto", "briefing"));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = { "CLIENTE" })
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar solicitação com briefing gigante (10.000 caracteres) excedendo o limite de 4.000")
        void testSolicitacaoComBriefingGigante() throws Exception {
            log.info("Elaborado pelo Gemini: Testando rejeição de briefing que excede 4000 caracteres.");
            TipoComissao tipo = obterTipoAnna();
            String briefingGigante = "Briefing extenso ".repeat(300);

            mockMvc.perform(post("/comissoes/solicitar")
                    .with(csrf())
                    .param("tipoComissaoId", tipo.getId().toString())
                    .param("briefing", briefingGigante))
                    .andExpect(status().isOk())
                    .andExpect(view().name("cliente/solicitar"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrors("solicitacaoDto", "briefing"));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = { "CLIENTE" })
        @DisplayName("Elaborado pelo Gemini: Deve impedir confirmação de pagamento para pedido em estado SOLICITADA (artista ainda não aceitou)")
        void testPagamentoEmEstadoInvalido() throws Exception {
            log.info("Elaborado pelo Gemini: Testando transição inválida de pagamento direto em pedido SOLICITADA.");
            Usuario cliente = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedido = new PedidoComissao("Quero um desenho em aquarela para teste",
                    new BigDecimal("150.00"), 5, cliente, anna, tipo);
            pedido = pedidoComissaoRepository.save(pedido);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/confirmar-pagamento").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attributeExists("erro"));
        }

        @Test
        @WithMockUser(username = "luan@artificio.local", roles = { "CLIENTE" })
        @DisplayName("Elaborado pelo Gemini: Deve barrar Cliente B tentando acessar pagamento do pedido do Cliente A (Anti-IDOR)")
        void testIdorAcessoPagamentoOutroCliente() throws Exception {
            log.info("Elaborado pelo Gemini: Testando proteção contra IDOR no acesso à tela de pagamento.");
            Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedidoEduardo = new PedidoComissao("Briefing privado do Eduardo", new BigDecimal("150.00"),
                    5, eduardo, anna, tipo);
            pedidoEduardo.setStatus(StatusComissao.AGUARDANDO_PAGAMENTO);
            pedidoEduardo = pedidoComissaoRepository.save(pedidoEduardo);

            mockMvc.perform(get("/comissoes/" + pedidoEduardo.getId() + "/pagamento"))
                    .andExpect(status().isBadRequest())
                    .andExpect(view().name("error"))
                    .andExpect(model().attribute("status", 400));
        }

        @Test
        @WithMockUser(username = "luan@artificio.local", roles = { "CLIENTE" })
        @DisplayName("Elaborado pelo Gemini: Deve barrar Cliente B tentando avaliar pedido concluído do Cliente A (Anti-IDOR)")
        void testIdorAvaliacaoPedidoOutroCliente() throws Exception {
            log.info("Elaborado pelo Gemini: Testando proteção contra IDOR na avaliação de pedido de terceiro.");
            Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedidoEduardo = new PedidoComissao("Briefing privado do Eduardo", new BigDecimal("150.00"),
                    5, eduardo, anna, tipo);
            pedidoEduardo.setStatus(StatusComissao.CONCLUIDA);
            pedidoEduardo = pedidoComissaoRepository.save(pedidoEduardo);

            mockMvc.perform(get("/comissoes/" + pedidoEduardo.getId() + "/avaliar"))
                    .andExpect(status().isBadRequest())
                    .andExpect(view().name("error"))
                    .andExpect(model().attribute("status", 400));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = { "CLIENTE" })
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar submissão de avaliação com nota inválida zero ou superior a cinco")
        void testAvaliacaoComNotaInvalida() throws Exception {
            log.info("Elaborado pelo Gemini: Testando validação de nota de avaliação fora do intervalo [1..5].");
            Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedido = new PedidoComissao("Pedido finalizado para avaliação", new BigDecimal("150.00"), 5,
                    eduardo, anna, tipo);
            pedido.setStatus(StatusComissao.CONCLUIDA);
            pedido = pedidoComissaoRepository.save(pedido);

            // Nota 0 (abaixo de 1)
            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/avaliar")
                    .with(csrf())
                    .param("pedidoId", pedido.getId().toString())
                    .param("nota", "0")
                    .param("comentario", "Nota zero não permitida"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("cliente/avaliar"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrors("novaAvaliacaoDto", "nota"));

            // Nota 999 (acima de 5)
            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/avaliar")
                    .with(csrf())
                    .param("pedidoId", pedido.getId().toString())
                    .param("nota", "999")
                    .param("comentario", "Nota extrema não permitida"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("cliente/avaliar"))
                    .andExpect(model().hasErrors())
                    .andExpect(model().attributeHasFieldErrors("novaAvaliacaoDto", "nota"));
        }

        @Test
        @WithMockUser(username = "cristiano@artificio.local", roles = { "ARTISTA" })
        @DisplayName("Elaborado pelo Gemini: Deve impedir artista de registrar entrega com link vazio")
        void testEntregaComLinkVazio() throws Exception {
            log.info("Elaborado pelo Gemini: Testando entrega com link de arte em branco.");
            UUID uuidAleatorio = UUID.randomUUID();

            mockMvc.perform(post("/comissoes/" + uuidAleatorio + "/entregar")
                    .with(csrf())
                    .param("linkEntrega", "   "))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attributeExists("erro"));
        }
    }

    // ------------------------------------------------------------------------------------------------
    // 7. CONTROLE DE ACESSO, PAPEIS (ROLES) E DEGRADAÇÃO ELEGANTE
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
        @WithMockUser(username = "eduardo@artificio.local", roles = { "CLIENTE" })
        @DisplayName("Elaborado pelo Gemini: Deve retornar HTTP 403 Forbidden quando cliente tentar acessar serviços de artista")
        void testClienteTentandoAcessarPainelArtista() throws Exception {
            log.info(
                    "Elaborado pelo Gemini: Testando isolamento horizontal/vertical: cliente acessando painel artista.");
            mockMvc.perform(get("/painel/artista/servicos"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = { "ARTISTA" })
        @DisplayName("Elaborado pelo Gemini: Deve retornar HTTP 403 Forbidden quando artista tentar acessar moderação de administração")
        void testArtistaTentandoAcessarAdmin() throws Exception {
            log.info("Elaborado pelo Gemini: Testando artista acessando área administrativa.");
            mockMvc.perform(get("/admin/usuarios"))
                    .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(username = "admin@artificio.local", roles = { "ADMIN" })
        @DisplayName("Elaborado pelo Gemini: Deve impedir administrador de contratar serviços por regra de segurança (403 Forbidden)")
        void testAdminImpedidoDeContratarServicos() throws Exception {
            log.info(
                    "Elaborado pelo Gemini: Testando regra de segurança impedindo administrador de acessar rotas de contratação de comissões.");
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = tipoComissaoRepository.findByArtistaIdAndAtivoTrue(anna.getId()).get(0);

            mockMvc.perform(get("/comissoes/solicitar/" + tipo.getId()))
                    .andExpect(status().isForbidden());
        }
    }

    // ------------------------------------------------------------------------------------------------
    // 8. TENTATIVAS DE MANIPULAÇÃO CRUZADA ENTRE ARTISTAS (ANTI-IDOR &
    // ANTI-TAMPERING)
    // ------------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Testes Gemini: Manipulação Cruzada Entre Artistas e Violações de Ciclo de Vida")
    class ManipulacaoCruzadaEntreArtistasTests {

        private TipoComissao obterTipoAnna() {
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            return tipoComissaoRepository.findByArtistaIdAndAtivoTrue(anna.getId()).get(0);
        }

        @Test
        @WithMockUser(username = "cristiano@artificio.local", roles = { "ARTISTA" })
        @DisplayName("Elaborado pelo Gemini: Deve impedir Artista B de aceitar pedido endereçado ao Artista A")
        void testArtistaBNaoPodeAceitarPedidoDoArtistaA() throws Exception {
            log.info("Elaborado pelo Gemini: Testando se Artista B consegue aceitar pedido do Artista A.");
            Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedidoParaAnna = new PedidoComissao("Desenho para Anna", new BigDecimal("150.00"), 5,
                    eduardo, anna, tipo);
            pedidoParaAnna = pedidoComissaoRepository.save(pedidoParaAnna);

            // Cristiano (Artista B) tenta aceitar o pedido da Anna
            mockMvc.perform(post("/comissoes/" + pedidoParaAnna.getId() + "/aceitar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas o artista destinatário pode aceitar este pedido"));
        }

        @Test
        @WithMockUser(username = "cristiano@artificio.local", roles = { "ARTISTA" })
        @DisplayName("Elaborado pelo Gemini: Deve impedir Artista B de recusar pedido endereçado ao Artista A")
        void testArtistaBNaoPodeRecusarPedidoDoArtistaA() throws Exception {
            log.info("Elaborado pelo Gemini: Testando se Artista B consegue recusar pedido do Artista A.");
            Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedidoParaAnna = new PedidoComissao("Desenho para Anna", new BigDecimal("150.00"), 5,
                    eduardo, anna, tipo);
            pedidoParaAnna = pedidoComissaoRepository.save(pedidoParaAnna);

            mockMvc.perform(post("/comissoes/" + pedidoParaAnna.getId() + "/recusar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas o artista destinatário pode recusar este pedido"));
        }

        @Test
        @WithMockUser(username = "cristiano@artificio.local", roles = { "ARTISTA" })
        @DisplayName("Elaborado pelo Gemini: Deve impedir Artista B de registrar entrega de trabalho do Artista A")
        void testArtistaBNaoPodeEntregarPedidoDoArtistaA() throws Exception {
            log.info(
                    "Elaborado pelo Gemini: Testando se Artista B consegue registrar entrega de trabalho do Artista A.");
            Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedidoAnna = new PedidoComissao("Desenho para Anna", new BigDecimal("150.00"), 5, eduardo,
                    anna, tipo);
            pedidoAnna.setStatus(StatusComissao.EM_ANDAMENTO);
            pedidoAnna = pedidoComissaoRepository.save(pedidoAnna);

            mockMvc.perform(post("/comissoes/" + pedidoAnna.getId() + "/entregar")
                    .with(csrf())
                    .param("linkEntrega", "https://entrega.falsa/arte.zip"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas o artista pode registrar a entrega do trabalho"));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = { "CLIENTE" })
        @DisplayName("Elaborado pelo Gemini: Deve impedir cliente de cancelar pedido quando a produção já foi iniciada")
        void testClienteNaoPodeCancelarPedidoEmAndamento() throws Exception {
            log.info("Elaborado pelo Gemini: Testando bloqueio de cancelamento após início da produção.");
            Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedido = new PedidoComissao("Desenho em produção", new BigDecimal("150.00"), 5, eduardo,
                    anna, tipo);
            pedido.setStatus(StatusComissao.EM_ANDAMENTO);
            pedido = pedidoComissaoRepository.save(pedido);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/cancelar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro",
                            "O pedido não pode ser cancelado após o início da produção ou pagamento"));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = { "CLIENTE" })
        @DisplayName("Elaborado pelo Gemini: Deve impedir solicitação de revisão para pedido que ainda não foi entregue")
        void testSolicitacaoRevisaoEmPedidoNaoEntregue() throws Exception {
            log.info("Elaborado pelo Gemini: Testando pedido de revisão para encomenda ainda em confecção.");
            Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedido = new PedidoComissao("Desenho não entregue", new BigDecimal("150.00"), 5, eduardo,
                    anna, tipo);
            pedido.setStatus(StatusComissao.EM_ANDAMENTO);
            pedido = pedidoComissaoRepository.save(pedido);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/revisao").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas pedidos entregues podem ter revisão solicitada"));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = { "CLIENTE" })
        @DisplayName("Elaborado pelo Gemini: Deve impedir aprovação de conclusão para pedido que ainda não foi entregue")
        void testAprovacaoConclusaoEmPedidoNaoEntregue() throws Exception {
            log.info("Elaborado pelo Gemini: Testando aprovação de comissão que sequer recebeu entrega.");
            Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedido = new PedidoComissao("Desenho inacabado", new BigDecimal("150.00"), 5, eduardo, anna,
                    tipo);
            pedido.setStatus(StatusComissao.SOLICITADA);
            pedido = pedidoComissaoRepository.save(pedido);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/aprovar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro",
                            "Apenas pedidos com arte entregue podem ser concluídos e aprovados"));
        }
    }

    // ------------------------------------------------------------------------------------------------
    // 9. RESILIÊNCIA E COBERTURA DE ERROS DO GLOBAL EXCEPTION HANDLER
    // ------------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Testes Gemini: Validação da Camada Global Defensiva e View error.html")
    class GlobalExceptionHandlerResilienciaTests {

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve garantir que tela de erro não vaza stack traces ou classes internas do Java/Spring")
        void testErrorViewNaoVazaStackTrace() throws Exception {
            log.info(
                    "Elaborado pelo Gemini: Verificando que a tela defensiva error.html não vaza detalhes internos de implementação.");
            mockMvc.perform(get("/artistas/parametro-invalido-para-forcar-erro"))
                    .andExpect(status().isBadRequest())
                    .andExpect(view().name("error"))
                    .andExpect(content().string(not(containsString("Exception in thread"))))
                    .andExpect(content().string(not(containsString("org.springframework"))))
                    .andExpect(content().string(not(containsString("at com.github."))))
                    .andExpect(content().string(containsString("Artifício")))
                    .andExpect(content().string(containsString("Voltar para o Início")));
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve retornar headers de segurança estritos na exibição de imagens (CSP, nosniff, cache)")
        void testHeadersDeSegurancaEmImagens() throws Exception {
            log.info("Elaborado pelo Gemini: Verificando headers de segurança HTTP contra sniffing e XSS em mídia.");
            ImagemBlob img = new ImagemBlob("img-teste-seguranca", "foto.png", "image/png",
                    new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47 });
            imagemBlobRepository.save(img);

            mockMvc.perform(get("/imagens/" + img.getId()))
                    .andExpect(status().isOk())
                    .andExpect(header().exists("Content-Security-Policy"))
                    .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                    .andExpect(header().string("Content-Security-Policy", containsString("sandbox")));
        }

        @Test
        @WithMockUser(username = "admin@artificio.local", roles = { "ADMIN" })
        @DisplayName("Elaborado pelo Gemini: Deve carregar área de administração de usuários com sucesso para ROLE_ADMIN")
        void testAdminAcessaListagemUsuarios() throws Exception {
            log.info("Elaborado pelo Gemini: Testando acesso legítimo de administrador.");
            mockMvc.perform(get("/admin/usuarios"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("admin/usuarios"))
                    .andExpect(model().attributeExists("usuarios"))
                    .andExpect(content().string(containsString("Supervisão de Usuários")));
        }
    }

    // ------------------------------------------------------------------------------------------------
    // 10. TENTATIVAS DE ESCALADA DE PRIVILÉGIOS E SEGURANÇA DE AUTO-CADASTRO
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
            TokenAtivacao token = new TokenAtivacao(tokenStr, novo, java.time.LocalDateTime.now().plusHours(1));
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
            TokenAtivacao token = new TokenAtivacao(tokenStr, novo, java.time.LocalDateTime.now().minusHours(2));
            tokenAtivacaoRepository.save(token);

            mockMvc.perform(get("/confirmar-email").param("token", tokenStr))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"))
                    .andExpect(flash().attribute("erro", "O link de confirmação expirou. Por favor, realize um novo cadastro."));
        }
    }

    // ------------------------------------------------------------------------------------------------
    // 11. MATRIZ COMPLETA DE TRANSIÇÕES INVÁLIDAS DA MÁQUINA DE ESTADOS (STATE MACHINE FUZZING)
    // ------------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Testes Gemini: Matriz Completa de Transições Inválidas de Pedidos")
    class MatrizCompletaMaquinaDeEstadosTests {

        private TipoComissao obterTipoAnna() {
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            return tipoComissaoRepository.findByArtistaIdAndAtivoTrue(anna.getId()).get(0);
        }

        private PedidoComissao criarPedido(StatusComissao status) {
            Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedido = new PedidoComissao("Pedido de teste de estado", new BigDecimal("150.00"), 5, eduardo, anna, tipo);
            pedido.setStatus(status);
            return pedidoComissaoRepository.save(pedido);
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir solicitação de comissão para modalidade desativada (ativo = false)")
        void testTentativaSolicitarComissaoEmModalidadeInativa() throws Exception {
            log.info("Elaborado pelo Gemini: Testando contratação de modalidade inativa.");
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao inativo = new TipoComissao("Arte Antiga", "Desc", "Aquarela", new BigDecimal("50.00"), 3, anna);
            inativo.setAtivo(false);
            inativo = tipoComissaoRepository.save(inativo);

            mockMvc.perform(post("/comissoes/solicitar")
                            .with(csrf())
                            .param("tipoComissaoId", inativo.getId().toString())
                            .param("briefing", "Tentando encomendar serviço desativado"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/"))
                    .andExpect(flash().attribute("erro", "Esta modalidade de serviço não está mais disponível para novas encomendas"));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir artista de aceitar pedido já CANCELADO")
        void testTentativaAceitarPedidoJaCancelado() throws Exception {
            log.info("Elaborado pelo Gemini: Testando tentativa de aceitar pedido cancelado.");
            PedidoComissao pedido = criarPedido(StatusComissao.CANCELADA);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/aceitar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas pedidos em estado SOLICITADA podem ser aceitos"));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir artista de aceitar pedido já RECUSADO")
        void testTentativaAceitarPedidoJaRecusado() throws Exception {
            log.info("Elaborado pelo Gemini: Testando tentativa de aceitar pedido recusado.");
            PedidoComissao pedido = criarPedido(StatusComissao.RECUSADA);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/aceitar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas pedidos em estado SOLICITADA podem ser aceitos"));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir artista de aceitar pedido que já está EM_ANDAMENTO")
        void testTentativaAceitarPedidoJaEmAndamento() throws Exception {
            log.info("Elaborado pelo Gemini: Testando tentativa de aceitar pedido em andamento.");
            PedidoComissao pedido = criarPedido(StatusComissao.EM_ANDAMENTO);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/aceitar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas pedidos em estado SOLICITADA podem ser aceitos"));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir artista de recusar pedido que já está PAGO")
        void testTentativaRecusarPedidoJaPago() throws Exception {
            log.info("Elaborado pelo Gemini: Testando tentativa de recusar pedido pago.");
            PedidoComissao pedido = criarPedido(StatusComissao.PAGO);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/recusar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas pedidos em estado SOLICITADA podem ser recusados"));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir artista de recusar pedido já CONCLUIDO")
        void testTentativaRecusarPedidoJaConcluido() throws Exception {
            log.info("Elaborado pelo Gemini: Testando tentativa de recusar pedido concluído.");
            PedidoComissao pedido = criarPedido(StatusComissao.CONCLUIDA);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/recusar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas pedidos em estado SOLICITADA podem ser recusados"));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir cliente de pagar pedido que já foi CANCELADO")
        void testTentativaPagarPedidoJaCancelado() throws Exception {
            log.info("Elaborado pelo Gemini: Testando pagamento em pedido cancelado.");
            PedidoComissao pedido = criarPedido(StatusComissao.CANCELADA);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/confirmar-pagamento").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "O pedido não está aguardando pagamento"));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir cliente de pagar pedido que já foi RECUSADO")
        void testTentativaPagarPedidoJaRecusado() throws Exception {
            log.info("Elaborado pelo Gemini: Testando pagamento em pedido recusado.");
            PedidoComissao pedido = criarPedido(StatusComissao.RECUSADA);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/confirmar-pagamento").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "O pedido não está aguardando pagamento"));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir cliente de pagar pedido que já está PAGO (pagamento duplo)")
        void testTentativaPagarPedidoJaPago() throws Exception {
            log.info("Elaborado pelo Gemini: Testando pagamento duplicado.");
            PedidoComissao pedido = criarPedido(StatusComissao.PAGO);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/confirmar-pagamento").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "O pedido não está aguardando pagamento"));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir início de produção de pedido em estado SOLICITADA sem pagamento")
        void testTentativaIniciarProducaoPedidoSolicitadaSemPagamento() throws Exception {
            log.info("Elaborado pelo Gemini: Testando início prematuro de produção em pedido não pago.");
            PedidoComissao pedido = criarPedido(StatusComissao.SOLICITADA);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/iniciar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas pedidos pagos podem ter a produção iniciada"));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir início de produção de pedido em AGUARDANDO_PAGAMENTO")
        void testTentativaIniciarProducaoPedidoAguardandoPagamento() throws Exception {
            log.info("Elaborado pelo Gemini: Testando início de produção de pedido aguardando pagamento.");
            PedidoComissao pedido = criarPedido(StatusComissao.AGUARDANDO_PAGAMENTO);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/iniciar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas pedidos pagos podem ter a produção iniciada"));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir registro de entrega de pedido em SOLICITADA")
        void testTentativaEntregarPedidoEmSolicitada() throws Exception {
            log.info("Elaborado pelo Gemini: Testando entrega de pedido em estado SOLICITADA.");
            PedidoComissao pedido = criarPedido(StatusComissao.SOLICITADA);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/entregar")
                            .with(csrf())
                            .param("linkEntrega", "https://entrega.arte/123"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attribute("erro", "O pedido deve estar em andamento para receber uma entrega"));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir registro de entrega de pedido em AGUARDANDO_PAGAMENTO")
        void testTentativaEntregarPedidoAguardandoPagamento() throws Exception {
            log.info("Elaborado pelo Gemini: Testando entrega de pedido em AGUARDANDO_PAGAMENTO.");
            PedidoComissao pedido = criarPedido(StatusComissao.AGUARDANDO_PAGAMENTO);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/entregar")
                            .with(csrf())
                            .param("linkEntrega", "https://entrega.arte/123"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/pedidos"))
                    .andExpect(flash().attribute("erro", "O pedido deve estar em andamento para receber uma entrega"));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir solicitação de revisão para pedido PAGO que ainda não foi entregue")
        void testTentativaSolicitarRevisaoPedidoPago() throws Exception {
            log.info("Elaborado pelo Gemini: Testando solicitação de revisão para pedido pago não entregue.");
            PedidoComissao pedido = criarPedido(StatusComissao.PAGO);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/revisao").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas pedidos entregues podem ter revisão solicitada"));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir aprovação de conclusão para pedido PAGO não entregue")
        void testTentativaAprovarConclusaoPedidoPago() throws Exception {
            log.info("Elaborado pelo Gemini: Testando aprovação de conclusão em pedido pago não entregue.");
            PedidoComissao pedido = criarPedido(StatusComissao.PAGO);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/aprovar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas pedidos com arte entregue podem ser concluídos e aprovados"));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir cancelamento de pedido já CONCLUIDO")
        void testTentativaCancelarPedidoJaConcluido() throws Exception {
            log.info("Elaborado pelo Gemini: Testando cancelamento de pedido já concluído.");
            PedidoComissao pedido = criarPedido(StatusComissao.CONCLUIDA);

            mockMvc.perform(post("/comissoes/" + pedido.getId() + "/cancelar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "O pedido não pode ser cancelado após o início da produção ou pagamento"));
        }
    }

    // ------------------------------------------------------------------------------------------------
    // 12. BATERIA AVANÇADA DE PROTEÇÃO ANTI-IDOR (MANIPULAÇÃO CRUZADA ENTRE USUÁRIOS)
    // ------------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Testes Gemini: Bateria Completa de Proteção Anti-IDOR")
    class BateriaAvancadaAntiIdorTests {

        private TipoComissao obterTipoAnna() {
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            return tipoComissaoRepository.findByArtistaIdAndAtivoTrue(anna.getId()).get(0);
        }

        @Test
        @WithMockUser(username = "cristiano@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir Artista B de alternar (toggle) status do serviço do Artista A")
        void testIdorArtistaBNaoPodeToggleServicoDoArtistaA() throws Exception {
            log.info("Elaborado pelo Gemini: Testando IDOR na alteração de status de serviço entre artistas.");
            TipoComissao tipoAnna = obterTipoAnna();

            mockMvc.perform(post("/painel/artista/servicos/" + tipoAnna.getId() + "/toggle").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/servicos"))
                    .andExpect(flash().attribute("erro", "O serviço não pertence ao artista logado"));
        }

        @Test
        @WithMockUser(username = "cristiano@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir Artista B de remover obra do portfólio do Artista A")
        void testIdorArtistaBNaoPodeRemoverObraDoArtistaA() throws Exception {
            log.info("Elaborado pelo Gemini: Testando IDOR na exclusão de obra do portfólio.");
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            ObraPortfolio obraAnna = new ObraPortfolio("Obra Anna", "Desc", "Aquarela", "uuid-fake", anna);
            obraAnna = obraPortfolioRepository.save(obraAnna);

            mockMvc.perform(post("/painel/artista/portfolio/" + obraAnna.getId() + "/remover").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/artista/portfolio"))
                    .andExpect(flash().attribute("erro", "A obra não pertence ao artista logado"));
        }

        @Test
        @WithMockUser(username = "luan@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir Cliente B de confirmar pagamento do pedido do Cliente A")
        void testIdorClienteBNaoPodeConfirmarPagamentoDoClienteA() throws Exception {
            log.info("Elaborado pelo Gemini: Testando IDOR no endpoint de confirmação de pagamento.");
            Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedidoEduardo = new PedidoComissao("Pedido Eduardo", new BigDecimal("150.00"), 5, eduardo, anna, tipo);
            pedidoEduardo.setStatus(StatusComissao.AGUARDANDO_PAGAMENTO);
            pedidoEduardo = pedidoComissaoRepository.save(pedidoEduardo);

            mockMvc.perform(post("/comissoes/" + pedidoEduardo.getId() + "/confirmar-pagamento").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas o cliente solicitante pode confirmar o pagamento"));
        }

        @Test
        @WithMockUser(username = "luan@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir Cliente B de cancelar o pedido do Cliente A")
        void testIdorClienteBNaoPodeCancelarPedidoDoClienteA() throws Exception {
            log.info("Elaborado pelo Gemini: Testando IDOR no endpoint de cancelamento de pedido.");
            Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedidoEduardo = new PedidoComissao("Pedido Eduardo", new BigDecimal("150.00"), 5, eduardo, anna, tipo);
            pedidoEduardo.setStatus(StatusComissao.SOLICITADA);
            pedidoEduardo = pedidoComissaoRepository.save(pedidoEduardo);

            mockMvc.perform(post("/comissoes/" + pedidoEduardo.getId() + "/cancelar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas o cliente solicitante pode cancelar este pedido"));
        }

        @Test
        @WithMockUser(username = "luan@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir Cliente B de aprovar a conclusão do pedido do Cliente A")
        void testIdorClienteBNaoPodeAprovarConclusaoDoClienteA() throws Exception {
            log.info("Elaborado pelo Gemini: Testando IDOR no endpoint de aprovação de conclusão.");
            Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedidoEduardo = new PedidoComissao("Pedido Eduardo", new BigDecimal("150.00"), 5, eduardo, anna, tipo);
            pedidoEduardo.setStatus(StatusComissao.ENTREGUE);
            pedidoEduardo = pedidoComissaoRepository.save(pedidoEduardo);

            mockMvc.perform(post("/comissoes/" + pedidoEduardo.getId() + "/aprovar").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas o cliente pode aprovar a conclusão da comissão"));
        }

        @Test
        @WithMockUser(username = "luan@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve impedir Cliente B de solicitar revisão do pedido do Cliente A")
        void testIdorClienteBNaoPodeSolicitarRevisaoDoClienteA() throws Exception {
            log.info("Elaborado pelo Gemini: Testando IDOR no endpoint de solicitação de revisão.");
            Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElseThrow();
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = obterTipoAnna();

            PedidoComissao pedidoEduardo = new PedidoComissao("Pedido Eduardo", new BigDecimal("150.00"), 5, eduardo, anna, tipo);
            pedidoEduardo.setStatus(StatusComissao.ENTREGUE);
            pedidoEduardo = pedidoComissaoRepository.save(pedidoEduardo);

            mockMvc.perform(post("/comissoes/" + pedidoEduardo.getId() + "/revisao").with(csrf()))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"))
                    .andExpect(flash().attribute("erro", "Apenas o cliente pode solicitar revisão da arte entregue"));
        }
    }

    // ------------------------------------------------------------------------------------------------
    // 13. VALIDAÇÃO DE ASSINATURAS MÁGICAS E TIPOS MIME DE IMAGEM
    // ------------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Testes Gemini: Validação Unitária de Assinaturas Binárias e Limites de Imagem")
    class AssinaturasMagicasETiposMimeTests {

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar arquivo PNG truncado com menos de 8 bytes de assinatura mágica")
        void testPngTruncadoApenasTresBytes() {
            log.info("Elaborado pelo Gemini: Testando arquivo PNG truncado.");
            byte[] pngTruncado = new byte[]{(byte) 0x89, 0x50, 0x4E};
            assertThatThrownBy(() -> validadorUploadImagem.validarEObterMimeType(pngTruncado, "image/png"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("não corresponde a uma imagem válida");
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar arquivo JPEG truncado com apenas 2 bytes")
        void testJpegTruncadoApenasDoisBytes() {
            log.info("Elaborado pelo Gemini: Testando arquivo JPEG truncado.");
            byte[] jpegTruncado = new byte[]{(byte) 0xFF, (byte) 0xD8};
            assertThatThrownBy(() -> validadorUploadImagem.validarEObterMimeType(jpegTruncado, "image/jpeg"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("não corresponde a uma imagem válida");
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar arquivo GIF truncado com menos de 6 bytes")
        void testGifTruncadoMenosDeSeisBytes() {
            log.info("Elaborado pelo Gemini: Testando arquivo GIF truncado.");
            byte[] gifTruncado = "GIF8".getBytes(StandardCharsets.US_ASCII);
            assertThatThrownBy(() -> validadorUploadImagem.validarEObterMimeType(gifTruncado, "image/gif"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("não corresponde a uma imagem válida");
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar arquivo WEBP sem cabeçalho RIFF")
        void testWebpSemCabecalhoRiff() {
            log.info("Elaborado pelo Gemini: Testando arquivo WEBP sem RIFF inicial.");
            byte[] webpSemRiff = "NAO_RIFF_123456789".getBytes(StandardCharsets.US_ASCII);
            assertThatThrownBy(() -> validadorUploadImagem.validarEObterMimeType(webpSemRiff, "image/webp"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("não corresponde a uma imagem válida");
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar arquivo WEBP com RIFF mas sem subtipo WEBP")
        void testWebpComRiffMasSemSubtipoWebp() {
            log.info("Elaborado pelo Gemini: Testando arquivo WEBP com subtipo falso.");
            byte[] riffWave = "RIFF1234WAVE5678".getBytes(StandardCharsets.US_ASCII);
            assertThatThrownBy(() -> validadorUploadImagem.validarEObterMimeType(riffWave, "image/webp"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("não corresponde a uma imagem válida");
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar tipos MIME não autorizados ou executáveis")
        void testMimeTypeDesconhecidoOuInseguro() {
            log.info("Elaborado pelo Gemini: Testando tipos MIME inseguros.");
            byte[] dados = "echo 1".getBytes(StandardCharsets.UTF_8);
            assertThatThrownBy(() -> validadorUploadImagem.validarEObterMimeType(dados, "application/x-sh"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Tipo de arquivo não suportado");
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar arquivo que excede o limite máximo permitido de 5MB")
        void testArquivoExcedeCincoMegabytes() {
            log.info("Elaborado pelo Gemini: Testando tamanho de arquivo excedendo 5MB.");
            byte[] arquivoGrande = new byte[ValidadorUploadImagem.TAMANHO_MAXIMO_BYTES + 1];
            assertThatThrownBy(() -> validadorUploadImagem.validarEObterMimeType(arquivoGrande, "image/png"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("excede o tamanho máximo permitido");
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve rejeitar array de bytes vazio ou nulo")
        void testArquivoVazioOuNulo() {
            log.info("Elaborado pelo Gemini: Testando arquivo de imagem vazio ou nulo.");
            assertThatThrownBy(() -> validadorUploadImagem.validarEObterMimeType(new byte[0], "image/png"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("não pode estar vazio");

            assertThatThrownBy(() -> validadorUploadImagem.validarEObterMimeType(null, "image/png"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("não pode estar vazio");
        }
    }

    // ------------------------------------------------------------------------------------------------
    // 14. VETORES SVG AVANÇADOS E HIGIENIZAÇÃO CONTRA STORED XSS
    // ------------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Testes Gemini: Vetores SVG Avançados e Proteção Contra XSS")
    class VetoresSvgAvancadosTests {

        private void assertSvgInvalido(String svgConteudo, String mensagemEsperada) {
            byte[] bytes = svgConteudo.getBytes(StandardCharsets.UTF_8);
            assertThatThrownBy(() -> validadorUploadImagem.validarEObterMimeType(bytes, "image/svg+xml"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(mensagemEsperada);
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve barrar SVG com tag SCRIPT em letras maiúsculas")
        void testSvgComTagScriptEmMaiusculas() {
            log.info("Elaborado pelo Gemini: Testando SVG com tag SCRIPT em maiúsculas.");
            String svg = "<svg xmlns='http://www.w3.org/2000/svg'><SCRIPT>alert(1)</SCRIPT></svg>";
            assertSvgInvalido(svg, "tags não permitidas");
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve barrar SVG com tag foreignObject")
        void testSvgComTagForeignObject() {
            log.info("Elaborado pelo Gemini: Testando SVG com tag foreignObject.");
            String svg = "<svg xmlns='http://www.w3.org/2000/svg'><foreignObject><div>x</div></foreignObject></svg>";
            assertSvgInvalido(svg, "tags não permitidas");
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve barrar SVG com tag iframe embutida")
        void testSvgComTagIframe() {
            log.info("Elaborado pelo Gemini: Testando SVG com tag iframe.");
            String svg = "<svg xmlns='http://www.w3.org/2000/svg'><iframe src='http://evil.com'></iframe></svg>";
            assertSvgInvalido(svg, "tags não permitidas");
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve barrar SVG com tag embed embutida")
        void testSvgComTagEmbed() {
            log.info("Elaborado pelo Gemini: Testando SVG com tag embed.");
            String svg = "<svg xmlns='http://www.w3.org/2000/svg'><embed src='payload.swf'></embed></svg>";
            assertSvgInvalido(svg, "tags não permitidas");
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve barrar SVG com tag object embutida")
        void testSvgComTagObject() {
            log.info("Elaborado pelo Gemini: Testando SVG com tag object.");
            String svg = "<svg xmlns='http://www.w3.org/2000/svg'><object data='test'></object></svg>";
            assertSvgInvalido(svg, "tags não permitidas");
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve barrar SVG contendo URI javascript: em atributos")
        void testSvgComEsquemaJavascriptEmHref() {
            log.info("Elaborado pelo Gemini: Testando SVG com esquema javascript:.");
            String svg = "<svg xmlns='http://www.w3.org/2000/svg'><a href='javascript:alert(1)'><circle r='5'/></a></svg>";
            assertSvgInvalido(svg, "URIs ou esquemas JavaScript");
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve barrar SVG contendo URI data:text/html")
        void testSvgComDataUriHtml() {
            log.info("Elaborado pelo Gemini: Testando SVG com data:text/html.");
            String svg = "<svg xmlns='http://www.w3.org/2000/svg'><image href='data:text/html;base64,PHNjcmlwdD4=' /></svg>";
            assertSvgInvalido(svg, "URIs ou esquemas JavaScript");
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve barrar SVG com atributo de evento onmouseover")
        void testSvgComAtributoOnMouseOver() {
            log.info("Elaborado pelo Gemini: Testando SVG com evento onmouseover.");
            String svg = "<svg xmlns='http://www.w3.org/2000/svg' onmouseover='alert(1)'></svg>";
            assertSvgInvalido(svg, "atributos de evento");
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve barrar SVG ausente da tag raiz <svg>")
        void testSvgSemTagSvg() {
            log.info("Elaborado pelo Gemini: Testando XML sem tag svg.");
            String svg = "<?xml version='1.0'?><note><body>Nao eh SVG</body></note>";
            assertSvgInvalido(svg, "tag <svg> ausente");
        }
    }

    // ------------------------------------------------------------------------------------------------
    // 15. FRONTEIRAS EXATAS DE VALIDAÇÃO BEAN VALIDATION (BOUNDARY VALUE TESTING)
    // ------------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Testes Gemini: Limites Exatos de Validação Bean Validation (Fronteiras)")
    class FronteirasExatasBeanValidationTests {

        @Test
        @DisplayName("Elaborado pelo Gemini: Cadastro deve aceitar nome com exatamente 2 caracteres (limite mínimo válido)")
        void testCadastroNomeExatamenteDoisCaracteres() throws Exception {
            log.info("Elaborado pelo Gemini: Testando limite mínimo de 2 caracteres no nome.");
            mockMvc.perform(post("/cadastro")
                            .with(csrf())
                            .param("nome", "Jo")
                            .param("email", "jo.minimo@artificio.local")
                            .param("senha", "Senha123")
                            .param("role", "ROLE_CLIENTE"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"));
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Cadastro deve aceitar nome com exatamente 150 caracteres (limite máximo válido)")
        void testCadastroNomeExatamenteCentoECinquentaCaracteres() throws Exception {
            log.info("Elaborado pelo Gemini: Testando limite máximo de 150 caracteres no nome.");
            String nome150 = "N".repeat(150);
            mockMvc.perform(post("/cadastro")
                            .with(csrf())
                            .param("nome", nome150)
                            .param("email", "nome150@artificio.local")
                            .param("senha", "Senha123")
                            .param("role", "ROLE_CLIENTE"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"));
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Cadastro deve rejeitar nome com 151 caracteres (limite máximo violado)")
        void testCadastroNomeCentoECinquentaEUmCaracteres() throws Exception {
            log.info("Elaborado pelo Gemini: Testando violação de 151 caracteres no nome.");
            String nome151 = "N".repeat(151);
            mockMvc.perform(post("/cadastro")
                            .with(csrf())
                            .param("nome", nome151)
                            .param("email", "nome151@artificio.local")
                            .param("senha", "Senha123")
                            .param("role", "ROLE_CLIENTE"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("cadastro"))
                    .andExpect(model().attributeHasFieldErrors("cadastroDto", "nome"));
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Cadastro deve rejeitar senha com 5 caracteres e aceitar com 6 caracteres")
        void testCadastroFronteiraSenhaSeisCaracteres() throws Exception {
            log.info("Elaborado pelo Gemini: Testando fronteira de senha de 5 vs 6 caracteres.");
            // 5 caracteres: erro
            mockMvc.perform(post("/cadastro")
                            .with(csrf())
                            .param("nome", "Senha Curta")
                            .param("email", "senha5@artificio.local")
                            .param("senha", "12345")
                            .param("role", "ROLE_CLIENTE"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeHasFieldErrors("cadastroDto", "senha"));

            // 6 caracteres: aceita
            mockMvc.perform(post("/cadastro")
                            .with(csrf())
                            .param("nome", "Senha Valida")
                            .param("email", "senha6@artificio.local")
                            .param("senha", "123456")
                            .param("role", "ROLE_CLIENTE"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/login"));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Serviço deve aceitar preço base de R$ 0,01 e rejeitar R$ 0,009")
        void testServicoFronteiraPrecoUmCentavo() throws Exception {
            log.info("Elaborado pelo Gemini: Testando limite inferior de preço base.");
            // 0.01: aceito
            mockMvc.perform(post("/painel/artista/servicos/novo")
                            .with(csrf())
                            .param("titulo", "Serviço Centavo")
                            .param("descricao", "Desc")
                            .param("categoria", "Aquarela")
                            .param("precoBase", "0.01")
                            .param("prazoEstimadoDias", "1"))
                    .andExpect(status().is3xxRedirection());

            // 0.009: rejeitado
            mockMvc.perform(post("/painel/artista/servicos/novo")
                            .with(csrf())
                            .param("titulo", "Serviço Centavo Fracionado")
                            .param("descricao", "Desc")
                            .param("categoria", "Aquarela")
                            .param("precoBase", "0.009")
                            .param("prazoEstimadoDias", "1"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeHasFieldErrors("novoTipoDto", "precoBase"));
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Serviço deve aceitar prazo de 365 dias e rejeitar 366 dias")
        void testServicoFronteiraPrazoTrezentosESessentaECincoDias() throws Exception {
            log.info("Elaborado pelo Gemini: Testando fronteira máxima de prazo de 365 vs 366 dias.");
            // 365 dias: aceito
            mockMvc.perform(post("/painel/artista/servicos/novo")
                            .with(csrf())
                            .param("titulo", "Serviço Um Ano")
                            .param("descricao", "Desc")
                            .param("categoria", "Aquarela")
                            .param("precoBase", "100.00")
                            .param("prazoEstimadoDias", "365"))
                    .andExpect(status().is3xxRedirection());

            // 366 dias: rejeitado
            mockMvc.perform(post("/painel/artista/servicos/novo")
                            .with(csrf())
                            .param("titulo", "Serviço Um Ano Mais Um Dia")
                            .param("descricao", "Desc")
                            .param("categoria", "Aquarela")
                            .param("precoBase", "100.00")
                            .param("prazoEstimadoDias", "366"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeHasFieldErrors("novoTipoDto", "prazoEstimadoDias"));
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Briefing deve aceitar exatamente 10 caracteres e rejeitar 9 caracteres")
        void testBriefingFronteiraDezCaracteres() throws Exception {
            log.info("Elaborado pelo Gemini: Testando fronteira de briefing de 9 vs 10 caracteres.");
            Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElseThrow();
            TipoComissao tipo = tipoComissaoRepository.findByArtistaIdAndAtivoTrue(anna.getId()).get(0);

            // 9 caracteres: erro
            mockMvc.perform(post("/comissoes/solicitar")
                            .with(csrf())
                            .param("tipoComissaoId", tipo.getId().toString())
                            .param("briefing", "123456789"))
                    .andExpect(status().isOk())
                    .andExpect(model().attributeHasFieldErrors("solicitacaoDto", "briefing"));

            // 10 caracteres: aceito
            mockMvc.perform(post("/comissoes/solicitar")
                            .with(csrf())
                            .param("tipoComissaoId", tipo.getId().toString())
                            .param("briefing", "1234567890"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/painel/cliente/pedidos"));
        }
    }

    // ------------------------------------------------------------------------------------------------
    // 16. PROTEÇÃO CSRF EM TODOS OS ENDPOINTS MUTÁVEIS (POST) DO SISTEMA
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

    // ------------------------------------------------------------------------------------------------
    // 17. INVERSÃO DE MÉTODOS HTTP E MÉTODOS NÃO PERMITIDOS (HTTP 405)
    // ------------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Testes Gemini: Inversão de Métodos HTTP e Verificação de 405 Method Not Allowed")
    class InversaoDeMetodosHttpTests {

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve retornar 405 Method Not Allowed para GET em aceite de pedido (exclusivo POST)")
        void testGetEmAceiteDePedidoRetorna405() throws Exception {
            log.info("Elaborado pelo Gemini: Testando GET em rota que só aceita POST de aceite.");
            mockMvc.perform(get("/comissoes/" + UUID.randomUUID() + "/aceitar"))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
        @DisplayName("Elaborado pelo Gemini: Deve retornar 405 Method Not Allowed para GET em recusa de pedido (exclusivo POST)")
        void testGetEmRecusaDePedidoRetorna405() throws Exception {
            log.info("Elaborado pelo Gemini: Testando GET em rota de recusa de pedido.");
            mockMvc.perform(get("/comissoes/" + UUID.randomUUID() + "/recusar"))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @WithMockUser(username = "eduardo@artificio.local", roles = {"CLIENTE"})
        @DisplayName("Elaborado pelo Gemini: Deve retornar 405 Method Not Allowed para GET em cancelamento de pedido (exclusivo POST)")
        void testGetEmCancelamentoDePedidoRetorna405() throws Exception {
            log.info("Elaborado pelo Gemini: Testando GET em rota de cancelamento de pedido.");
            mockMvc.perform(get("/comissoes/" + UUID.randomUUID() + "/cancelar"))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @WithMockUser(username = "admin@artificio.local", roles = {"ADMIN"})
        @DisplayName("Elaborado pelo Gemini: Deve retornar 405 Method Not Allowed para GET em promoção de usuário")
        void testGetEmPromocaoAdminRetorna405() throws Exception {
            log.info("Elaborado pelo Gemini: Testando GET em rota de promoção de usuário.");
            mockMvc.perform(get("/admin/usuarios/" + UUID.randomUUID() + "/promover"))
                    .andExpect(status().isMethodNotAllowed());
        }

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve retornar 405 Method Not Allowed para POST no catálogo inicial (exclusivo GET)")
        void testPostEmCatalogoInicialRetorna405() throws Exception {
            log.info("Elaborado pelo Gemini: Testando POST no catálogo público inicial.");
            mockMvc.perform(post("/").with(csrf()))
                    .andExpect(status().isMethodNotAllowed());
        }
    }

    // ------------------------------------------------------------------------------------------------
    // 18. TESTES DE INTERFACE WEB (HTML THYMELEAF, LAYOUT E SEGURANÇA DE RENDERIZAÇÃO)
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
}

