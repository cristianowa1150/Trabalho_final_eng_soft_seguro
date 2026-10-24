package com.github.trabalhoengsoftseguro.artificio.gemini;

import com.github.trabalhoengsoftseguro.artificio.artista.TipoComissao;
import com.github.trabalhoengsoftseguro.artificio.imagem.ImagemBlob;
import com.github.trabalhoengsoftseguro.artificio.usuario.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Testes Gemini: Validação de Entrada, Limites de Fronteira, Métodos HTTP e Resiliência Global")
public class GeminiValidacaoResilienciaHttpTests extends GeminiBaseTest {

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
            log.info("Elaborado pelo Gemini: Testando resiliência do repositório JPA contra payloads de injeção em parâmetros de busca.");
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
    // 2. ARTISTA E SERVIÇOS: PREÇOS E PRAZOS NEGATIVOS E ASTRONÔMICOS
    // ------------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Testes Gemini: Serviços com Preços Negativos, Prazos Nulos e Valores Extremos")
    class ArtistaEServicosValoresExtremosTests {

        @Test
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
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
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
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
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
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
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
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
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
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
        @WithMockUser(username = "anna@artificio.local", roles = {"ARTISTA"})
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
    // 3. RESILIÊNCIA E COBERTURA DE ERROS DO GLOBAL EXCEPTION HANDLER
    // ------------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Testes Gemini: Validação da Camada Global Defensiva e View error.html")
    class GlobalExceptionHandlerResilienciaTests {

        @Test
        @DisplayName("Elaborado pelo Gemini: Deve garantir que tela de erro não vaza stack traces ou classes internas do Java/Spring")
        void testErrorViewNaoVazaStackTrace() throws Exception {
            log.info("Elaborado pelo Gemini: Verificando que a tela defensiva error.html não vaza detalhes internos de implementação.");
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
                    new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47});
            imagemBlobRepository.save(img);

            mockMvc.perform(get("/imagens/" + img.getId()))
                    .andExpect(status().isOk())
                    .andExpect(header().exists("Content-Security-Policy"))
                    .andExpect(header().string("X-Content-Type-Options", "nosniff"))
                    .andExpect(header().string("Content-Security-Policy", containsString("sandbox")));
        }

        @Test
        @WithMockUser(username = "admin@artificio.local", roles = {"ADMIN"})
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
    // 4. FRONTEIRAS EXATAS DE VALIDAÇÃO BEAN VALIDATION (BOUNDARY VALUE TESTING)
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
    // 5. INVERSÃO DE MÉTODOS HTTP E MÉTODOS NÃO PERMITIDOS (HTTP 405)
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
}
