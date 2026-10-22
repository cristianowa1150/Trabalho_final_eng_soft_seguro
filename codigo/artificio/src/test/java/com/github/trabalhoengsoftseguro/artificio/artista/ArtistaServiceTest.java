package com.github.trabalhoengsoftseguro.artificio.artista;

import com.github.trabalhoengsoftseguro.artificio.avaliacao.AvaliacaoRepository;
import com.github.trabalhoengsoftseguro.artificio.imagem.ImagemService;
import com.github.trabalhoengsoftseguro.artificio.usuario.Role;
import com.github.trabalhoengsoftseguro.artificio.usuario.Usuario;
import com.github.trabalhoengsoftseguro.artificio.usuario.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistaServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private ObraPortfolioRepository obraPortfolioRepository;

    @Mock
    private TipoComissaoRepository tipoComissaoRepository;

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private ImagemService imagemService;

    private ArtistaService service;

    @BeforeEach
    void setUp() {
        service = new ArtistaService(
                usuarioRepository,
                obraPortfolioRepository,
                tipoComissaoRepository,
                avaliacaoRepository,
                imagemService
        );
    }

    @Test
    @DisplayName("Deve adicionar obra ao portfolio salvando imagem BLOB e persistindo")
    void deveAdicionarObraAoPortfolioComSucesso() {
        Usuario artista = new Usuario("Renata", "renata@artificio.local", "hash", Role.ROLE_ARTISTA, "Bio", "pix");
        artista.setId(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(artista));
        when(imagemService.salvar(any(byte[].class), anyString(), anyString())).thenReturn("uuid-blob-77");
        when(obraPortfolioRepository.save(any(ObraPortfolio.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NovaObraDto dto = new NovaObraDto("Dragão de Fogo", "Ilustração fantástica", "Fantasia");
        ObraPortfolio obra = service.adicionarObra(1L, dto, new byte[]{1, 2, 3}, "dragao.png", "image/png");

        assertNotNull(obra);
        assertEquals("Dragão de Fogo", obra.getTitulo());
        assertEquals("uuid-blob-77", obra.getImagemId());
        verify(imagemService).salvar(any(byte[].class), eq("dragao.png"), eq("image/png"));
        verify(obraPortfolioRepository).save(any(ObraPortfolio.class));
    }

    @Test
    @DisplayName("Deve rejeitar adicao de obra para usuario que nao seja artista")
    void deveRejeitarAdicionarObraParaNaoArtista() {
        Usuario cliente = new Usuario("Lucas", "lucas@artificio.local", "hash", Role.ROLE_CLIENTE, null, null);
        cliente.setId(2L);
        when(usuarioRepository.findById(2L)).thenReturn(Optional.of(cliente));

        NovaObraDto dto = new NovaObraDto("Obra", "Desc", "Cat");

        assertThrows(IllegalArgumentException.class,
                () -> service.adicionarObra(2L, dto, new byte[]{1}, "img.png", "image/png"));

        verify(obraPortfolioRepository, never()).save(any(ObraPortfolio.class));
        verify(imagemService, never()).salvar(any(), any(), any());
    }

    @Test
    @DisplayName("Deve remover obra e sua respectiva imagem quando artista e o proprietario")
    void deveRemoverObraComSucesso() {
        Usuario artista = new Usuario("Renata", "renata@artificio.local", "hash", Role.ROLE_ARTISTA, "Bio", "pix");
        artista.setId(1L);

        ObraPortfolio obra = new ObraPortfolio("Arte", "Desc", "Cat", "uuid-imagem-99", artista);
        obra.setId(10L);

        when(obraPortfolioRepository.findById(10L)).thenReturn(Optional.of(obra));

        service.removerObra(1L, 10L);

        verify(imagemService).remover("uuid-imagem-99");
        verify(obraPortfolioRepository).delete(obra);
    }

    @Test
    @DisplayName("Deve impedir que outro artista remova obra de terceiro")
    void deveRejeitarRemoverObraDeOutroArtista() {
        Usuario artistaDono = new Usuario("Renata", "renata@artificio.local", "hash", Role.ROLE_ARTISTA, "Bio", "pix");
        artistaDono.setId(1L);

        ObraPortfolio obra = new ObraPortfolio("Arte", "Desc", "Cat", "uuid-imagem-99", artistaDono);
        obra.setId(10L);

        when(obraPortfolioRepository.findById(10L)).thenReturn(Optional.of(obra));

        // Artista 2 tentando remover obra do Artista 1
        assertThrows(IllegalArgumentException.class, () -> service.removerObra(2L, 10L));

        verify(obraPortfolioRepository, never()).delete(any());
        verify(imagemService, never()).remover(any());
    }

    @Test
    @DisplayName("Deve salvar novo tipo de comissao com sucesso")
    void deveSalvarTipoComissaoComSucesso() {
        Usuario artista = new Usuario("Renata", "renata@artificio.local", "hash", Role.ROLE_ARTISTA, "Bio", "pix");
        artista.setId(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(artista));
        when(tipoComissaoRepository.save(any(TipoComissao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NovoTipoComissaoDto dto = new NovoTipoComissaoDto("Retrato Digital", "Busto colorido", "Digital", new BigDecimal("200.00"), 7);
        TipoComissao tipo = service.salvarTipoComissao(1L, dto);

        assertNotNull(tipo);
        assertEquals("Retrato Digital", tipo.getTitulo());
        assertEquals(new BigDecimal("200.00"), tipo.getPrecoBase());
        verify(tipoComissaoRepository).save(any(TipoComissao.class));
    }

    @Test
    @DisplayName("Deve obter perfil completo do artista com metricas e avaliacoes")
    void deveObterPerfilCompletoComEstatisticas() {
        Usuario artista = new Usuario("Renata", "renata@artificio.local", "hash", Role.ROLE_ARTISTA, "Bio", "pix");
        artista.setId(1L);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(artista));
        when(obraPortfolioRepository.findByArtistaIdOrderByDataCriacaoDesc(1L)).thenReturn(List.of(new ObraPortfolio()));
        when(tipoComissaoRepository.findByArtistaIdAndAtivoTrue(1L)).thenReturn(List.of(new TipoComissao()));
        when(avaliacaoRepository.calcularMediaNotasPorArtista(1L)).thenReturn(4.8);
        when(avaliacaoRepository.countByArtistaId(1L)).thenReturn(12L);

        ArtistaPerfilDto perfil = service.obterPerfilCompleto(1L);

        assertNotNull(perfil);
        assertEquals(4.8, perfil.mediaNotas());
        assertEquals(12L, perfil.totalAvaliacoes());
        assertEquals(1, perfil.obras().size());
        assertEquals(1, perfil.tiposComissao().size());
    }

    @Test
    @DisplayName("Deve delegar busca de artistas para repositorio com parametros normalizados")
    void deveDelegarBuscaComFiltrosNormalizados() {
        Usuario artista = new Usuario("Renata", "renata@artificio.local", "hash", Role.ROLE_ARTISTA, "Bio", "pix");
        when(usuarioRepository.buscarArtistasFiltrados(Role.ROLE_ARTISTA, "Pixel Art", "cyber"))
                .thenReturn(List.of(artista));

        List<Usuario> resultado = service.buscarArtistasComFiltros("  Pixel Art  ", "  cyber  ");

        assertEquals(1, resultado.size());
        assertEquals("Renata", resultado.get(0).getNome());
        verify(usuarioRepository).buscarArtistasFiltrados(Role.ROLE_ARTISTA, "Pixel Art", "cyber");
    }

    @Test
    @DisplayName("Deve passar null para repositorio quando categoria ou termo forem vazios ou apenas espacos")
    void devePassarNullQuandoFiltrosForemVazios() {
        when(usuarioRepository.buscarArtistasFiltrados(Role.ROLE_ARTISTA, null, null))
                .thenReturn(List.of());

        List<Usuario> resultado = service.buscarArtistasComFiltros("   ", "");

        assertNotNull(resultado);
        verify(usuarioRepository).buscarArtistasFiltrados(Role.ROLE_ARTISTA, null, null);
    }
}
