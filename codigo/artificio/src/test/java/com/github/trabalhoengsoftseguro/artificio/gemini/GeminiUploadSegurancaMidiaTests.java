package com.github.trabalhoengsoftseguro.artificio.gemini;

import com.github.trabalhoengsoftseguro.artificio.imagem.ValidadorUploadImagem;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@DisplayName("Testes Gemini: Upload de Mídia, Assinaturas Mágicas (Magic Bytes) e Sanitização SVG contra XSS")
public class GeminiUploadSegurancaMidiaTests extends GeminiBaseTest {

    // ------------------------------------------------------------------------------------------------
    // 1. UPLOAD DE IMAGENS E PORTFÓLIOS COM DADOS ANÔMALOS
    // ------------------------------------------------------------------------------------------------
    @Nested
    @DisplayName("Testes Gemini: Upload de Imagens e Portfólios Anômalos")
    class UploadDeImagensEPortfoliosAnomalosTests {

        @Test
        @WithMockUser(username = "cristiano@artificio.local", roles = {"ARTISTA"})
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
        @WithMockUser(username = "cristiano@artificio.local", roles = {"ARTISTA"})
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
        @WithMockUser(username = "cristiano@artificio.local", roles = {"ARTISTA"})
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
        @WithMockUser(username = "cristiano@artificio.local", roles = {"ARTISTA"})
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
        @WithMockUser(username = "cristiano@artificio.local", roles = {"ARTISTA"})
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
    // 2. VALIDAÇÃO DE ASSINATURAS MÁGICAS E TIPOS MIME DE IMAGEM
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
    // 3. VETORES SVG AVANÇADOS E HIGIENIZAÇÃO CONTRA STORED XSS
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
}
