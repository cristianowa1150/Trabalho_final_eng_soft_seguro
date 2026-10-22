package com.github.trabalhoengsoftseguro.artificio.artista;

import com.github.trabalhoengsoftseguro.artificio.avaliacao.AvaliacaoRepository;
import com.github.trabalhoengsoftseguro.artificio.imagem.ImagemService;
import com.github.trabalhoengsoftseguro.artificio.usuario.Role;
import com.github.trabalhoengsoftseguro.artificio.usuario.Usuario;
import com.github.trabalhoengsoftseguro.artificio.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class ArtistaService {

    private final UsuarioRepository usuarioRepository;
    private final ObraPortfolioRepository obraPortfolioRepository;
    private final TipoComissaoRepository tipoComissaoRepository;
    private final AvaliacaoRepository avaliacaoRepository;
    private final ImagemService imagemService;

    public ArtistaService(UsuarioRepository usuarioRepository,
                          ObraPortfolioRepository obraPortfolioRepository,
                          TipoComissaoRepository tipoComissaoRepository,
                          AvaliacaoRepository avaliacaoRepository,
                          ImagemService imagemService) {
        this.usuarioRepository = usuarioRepository;
        this.obraPortfolioRepository = obraPortfolioRepository;
        this.tipoComissaoRepository = tipoComissaoRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.imagemService = imagemService;
    }

    public List<Usuario> buscarArtistasComFiltros(String categoria, String termo) {
        String catNorm = (categoria != null && !categoria.isBlank()) ? categoria.trim() : null;
        String termoNorm = (termo != null && !termo.isBlank()) ? termo.trim() : null;

        return usuarioRepository.buscarArtistasFiltrados(Role.ROLE_ARTISTA, catNorm, termoNorm);
    }

    public ArtistaPerfilDto obterPerfilCompleto(UUID artistaId) {
        Usuario artista = usuarioRepository.findById(artistaId)
                .orElseThrow(() -> new IllegalArgumentException("Artista não encontrado com ID: " + artistaId));

        if (!artista.isArtista()) {
            throw new IllegalArgumentException("O usuário informado não possui perfil de artista");
        }

        List<ObraPortfolio> obras = obraPortfolioRepository.findByArtistaIdOrderByDataCriacaoDesc(artistaId);
        List<TipoComissao> tipos = tipoComissaoRepository.findByArtistaIdAndAtivoTrue(artistaId);
        Double media = avaliacaoRepository.calcularMediaNotasPorArtista(artistaId);
        long total = avaliacaoRepository.countByArtistaId(artistaId);

        return new ArtistaPerfilDto(artista, obras, tipos, media != null ? media : 0.0, total);
    }

    @Transactional
    public ObraPortfolio adicionarObra(UUID artistaId, NovaObraDto dto, byte[] bytesArquivo, String nomeArquivo, String contentType) {
        if (dto == null) {
            throw new IllegalArgumentException("Dados da obra não podem ser nulos");
        }
        Usuario artista = usuarioRepository.findById(artistaId)
                .orElseThrow(() -> new IllegalArgumentException("Artista não encontrado com ID: " + artistaId));

        if (!artista.isArtista()) {
            throw new IllegalArgumentException("Apenas contas com papel de Artista podem publicar obras");
        }

        String imagemId = imagemService.salvar(bytesArquivo, nomeArquivo, contentType);
        ObraPortfolio obra = new ObraPortfolio(dto.titulo(), dto.descricao(), dto.categoria(), imagemId, artista);

        return obraPortfolioRepository.save(obra);
    }

    @Transactional
    public void removerObra(UUID artistaId, UUID obraId) {
        ObraPortfolio obra = obraPortfolioRepository.findById(obraId)
                .orElseThrow(() -> new IllegalArgumentException("Obra não encontrada com ID: " + obraId));

        if (!obra.getArtista().getId().equals(artistaId)) {
            throw new IllegalArgumentException("A obra não pertence ao artista logado");
        }

        imagemService.remover(obra.getImagemId());
        obraPortfolioRepository.delete(obra);
    }

    @Transactional
    public TipoComissao salvarTipoComissao(UUID artistaId, NovoTipoComissaoDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Dados do tipo de comissão não podem ser nulos");
        }
        Usuario artista = usuarioRepository.findById(artistaId)
                .orElseThrow(() -> new IllegalArgumentException("Artista não encontrado com ID: " + artistaId));

        if (!artista.isArtista()) {
            throw new IllegalArgumentException("Apenas contas com papel de Artista podem cadastrar modalidades de comissão");
        }

        TipoComissao tipo = new TipoComissao(
                dto.titulo(),
                dto.descricao(),
                dto.categoria(),
                dto.precoBase(),
                dto.prazoEstimadoDias(),
                artista
        );

        return tipoComissaoRepository.save(tipo);
    }

    @Transactional
    public void alternarStatusTipoComissao(UUID artistaId, UUID tipoId, boolean ativo) {
        TipoComissao tipo = tipoComissaoRepository.findById(tipoId)
                .orElseThrow(() -> new IllegalArgumentException("Tipo de comissão não encontrado com ID: " + tipoId));

        if (!tipo.getArtista().getId().equals(artistaId)) {
            throw new IllegalArgumentException("O serviço não pertence ao artista logado");
        }

        tipo.setAtivo(ativo);
        tipoComissaoRepository.save(tipo);
    }

    public List<ObraPortfolio> listarObrasDoArtista(UUID artistaId) {
        return obraPortfolioRepository.findByArtistaIdOrderByDataCriacaoDesc(artistaId);
    }

    public List<TipoComissao> listarTiposComissaoDoArtista(UUID artistaId) {
        return tipoComissaoRepository.findByArtistaId(artistaId);
    }
}
