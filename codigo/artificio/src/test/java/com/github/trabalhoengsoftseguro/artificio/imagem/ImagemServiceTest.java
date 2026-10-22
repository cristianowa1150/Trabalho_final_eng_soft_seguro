package com.github.trabalhoengsoftseguro.artificio.imagem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImagemServiceTest {

    private static final byte[] VALID_PNG = new byte[]{(byte) 0x89, 0x50, 0x4E, 0x47, 0x0D, 0x0A, 0x1A, 0x0A, 0, 0, 0, 0};
    private static final byte[] VALID_JPEG = new byte[]{(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0, 0, 0x10, 0x4A, 0x46};
    private static final byte[] VALID_GIF = "GIF89a\1\0\1\0\0\0\0".getBytes(StandardCharsets.US_ASCII);
    private static final byte[] VALID_WEBP = new byte[]{
            'R', 'I', 'F', 'F',
            20, 0, 0, 0,
            'W', 'E', 'B', 'P',
            'V', 'P', '8', ' '
    };

    @Mock
    private ImagemBlobRepository repository;

    private ImagemService service;

    @BeforeEach
    void setUp() {
        service = new ImagemService(repository, new ValidadorUploadImagem());
    }

    @Test
    @DisplayName("Deve salvar imagem PNG valida com sucesso gerando UUID")
    void deveSalvarImagemComSucesso() {
        when(repository.save(any(ImagemBlob.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String idGerado = service.salvar(VALID_PNG, "arte.png", "image/png");

        assertNotNull(idGerado);
        assertFalse(idGerado.isBlank());
        verify(repository).save(any(ImagemBlob.class));
    }

    @Test
    @DisplayName("Deve salvar imagem JPEG e normalizar contentType image/jpg")
    void deveSalvarJpegComSucesso() {
        ArgumentCaptor<ImagemBlob> captor = ArgumentCaptor.forClass(ImagemBlob.class);
        when(repository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        String id = service.salvar(VALID_JPEG, "foto.jpg", "image/jpg");

        assertNotNull(id);
        assertEquals("image/jpeg", captor.getValue().getContentType());
    }

    @Test
    @DisplayName("Deve salvar SVG valido sem scripts")
    void deveSalvarSvgValidoComSucesso() {
        byte[] svg = "<svg xmlns='http://www.w3.org/2000/svg'><circle cx='50' cy='50' r='40'/></svg>".getBytes(StandardCharsets.UTF_8);
        when(repository.save(any(ImagemBlob.class))).thenAnswer(invocation -> invocation.getArgument(0));

        String id = service.salvar(svg, "vetor.svg", "image/svg+xml");

        assertNotNull(id);
        verify(repository).save(any(ImagemBlob.class));
    }

    @Test
    @DisplayName("Deve descartar nome original inseguro e atribuir nome gerado aleatoriamente com extensao confiavel")
    void deveDescartarNomeOriginalEGerarNomeAleatorio() {
        ArgumentCaptor<ImagemBlob> captor = ArgumentCaptor.forClass(ImagemBlob.class);
        when(repository.save(captor.capture())).thenAnswer(invocation -> invocation.getArgument(0));

        service.salvar(VALID_PNG, "../../../etc/passwd.exe", "image/png");

        String nomeSalvo = captor.getValue().getNomeOriginal();
        assertNotNull(nomeSalvo);
        assertFalse(nomeSalvo.contains("passwd"));
        assertFalse(nomeSalvo.contains(".."));
        assertTrue(nomeSalvo.endsWith(".png"));
    }

    @Test
    @DisplayName("Deve rejeitar conteudo vazio sem chamar o repositorio")
    void deveRejeitarConteudoVazio() {
        assertThrows(IllegalArgumentException.class,
                () -> service.salvar(new byte[0], "vazio.png", "image/png"));

        verify(repository, never()).save(any(ImagemBlob.class));
    }

    @Test
    @DisplayName("Deve rejeitar arquivo que ultrapassa o tamanho maximo de 5MB")
    void deveRejeitarArquivoMaiorQue5MB() {
        byte[] grande = new byte[5 * 1024 * 1024 + 1];

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.salvar(grande, "grande.png", "image/png"));

        assertTrue(ex.getMessage().contains("5MB"));
        verify(repository, never()).save(any(ImagemBlob.class));
    }

    @Test
    @DisplayName("Deve rejeitar tipo MIME nao permitido como text/html ou application/pdf")
    void deveRejeitarTipoMimeNaoPermitido() {
        byte[] dados = "<html><body>Ola</body></html>".getBytes(StandardCharsets.UTF_8);

        assertThrows(IllegalArgumentException.class,
                () -> service.salvar(dados, "pagina.html", "text/html"));

        verify(repository, never()).save(any(ImagemBlob.class));
    }

    @Test
    @DisplayName("Deve rejeitar imagem binaria cuja assinatura/magic bytes nao confere com o contentType")
    void deveRejeitarMagicBytesInvalidos() {
        byte[] dadosFalsos = "nao-sou-png".getBytes(StandardCharsets.UTF_8);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.salvar(dadosFalsos, "falso.png", "image/png"));

        assertTrue(ex.getMessage().contains("não corresponde"));
        verify(repository, never()).save(any(ImagemBlob.class));
    }

    @Test
    @DisplayName("Deve rejeitar SVG sem tag <svg>")
    void deveRejeitarSvgSemTagSvg() {
        byte[] svgInvalido = "<xml><teste>Ola</teste></xml>".getBytes(StandardCharsets.UTF_8);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.salvar(svgInvalido, "invalido.svg", "image/svg+xml"));

        assertTrue(ex.getMessage().contains("tag <svg> ausente"));
    }

    @Test
    @DisplayName("Deve rejeitar SVG com tag <script> para prevencao de Stored XSS")
    void deveRejeitarSvgComScript() {
        byte[] svgXss = "<svg xmlns='http://www.w3.org/2000/svg'><script>alert(document.cookie)</script></svg>".getBytes(StandardCharsets.UTF_8);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.salvar(svgXss, "ataque.svg", "image/svg+xml"));

        assertTrue(ex.getMessage().contains("tags não permitidas"));
        verify(repository, never()).save(any(ImagemBlob.class));
    }

    @Test
    @DisplayName("Deve rejeitar SVG com manipuladores de eventos como onload ou onerror")
    void deveRejeitarSvgComEventHandler() {
        byte[] svgXss = "<svg xmlns='http://www.w3.org/2000/svg' onload='alert(1)'></svg>".getBytes(StandardCharsets.UTF_8);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.salvar(svgXss, "ataque.svg", "image/svg+xml"));

        assertTrue(ex.getMessage().contains("atributos de evento"));
        verify(repository, never()).save(any(ImagemBlob.class));
    }

    @Test
    @DisplayName("Deve rejeitar SVG contendo tag <foreignObject>")
    void deveRejeitarSvgComForeignObject() {
        byte[] svgXss = "<svg><foreignObject><body xmlns='http://www.w3.org/1999/xhtml'>xss</body></foreignObject></svg>".getBytes(StandardCharsets.UTF_8);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.salvar(svgXss, "ataque.svg", "image/svg+xml"));

        assertTrue(ex.getMessage().contains("tags não permitidas"));
        verify(repository, never()).save(any(ImagemBlob.class));
    }

    @Test
    @DisplayName("Deve rejeitar SVG com esquema javascript:")
    void deveRejeitarSvgComJavascriptUri() {
        byte[] svgXss = "<svg xmlns='http://www.w3.org/2000/svg'><a href='javascript:alert(1)'><circle r='10'/></a></svg>".getBytes(StandardCharsets.UTF_8);

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.salvar(svgXss, "ataque.svg", "image/svg+xml"));

        assertTrue(ex.getMessage().contains("URIs ou esquemas JavaScript"));
        verify(repository, never()).save(any(ImagemBlob.class));
    }

    @Test
    @DisplayName("Deve recuperar imagem existente por ID")
    void deveRecuperarImagemExistente() {
        ImagemBlob blob = new ImagemBlob("uuid-123", "foto.jpg", "image/jpeg", VALID_JPEG);
        when(repository.findById("uuid-123")).thenReturn(Optional.of(blob));

        ImagemBlob recuperado = service.recuperar("uuid-123");

        assertEquals("uuid-123", recuperado.getId());
        assertEquals("image/jpeg", recuperado.getContentType());
        verify(repository).findById("uuid-123");
    }

    @Test
    @DisplayName("Deve lancar excecao ao tentar recuperar ID inexistente")
    void deveLancarExcecaoParaIdInexistente() {
        when(repository.findById("inexistente")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> service.recuperar("inexistente"));
    }

    @Test
    @DisplayName("Deve remover imagem quando ela existir no repositorio")
    void deveRemoverImagemQuandoExistir() {
        when(repository.existsById("uuid-123")).thenReturn(true);

        service.remover("uuid-123");

        verify(repository).deleteById("uuid-123");
    }
}
