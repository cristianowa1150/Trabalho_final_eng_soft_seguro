package com.github.trabalhoengsoftseguro.artificio;

import com.github.trabalhoengsoftseguro.artificio.artista.ObraPortfolio;
import com.github.trabalhoengsoftseguro.artificio.artista.ObraPortfolioRepository;
import com.github.trabalhoengsoftseguro.artificio.artista.TipoComissao;
import com.github.trabalhoengsoftseguro.artificio.artista.TipoComissaoRepository;
import com.github.trabalhoengsoftseguro.artificio.avaliacao.Avaliacao;
import com.github.trabalhoengsoftseguro.artificio.avaliacao.AvaliacaoRepository;
import com.github.trabalhoengsoftseguro.artificio.comissao.PedidoComissao;
import com.github.trabalhoengsoftseguro.artificio.comissao.PedidoComissaoRepository;
import com.github.trabalhoengsoftseguro.artificio.comissao.StatusComissao;
import com.github.trabalhoengsoftseguro.artificio.imagem.ImagemBlob;
import com.github.trabalhoengsoftseguro.artificio.imagem.ImagemBlobRepository;
import com.github.trabalhoengsoftseguro.artificio.usuario.Role;
import com.github.trabalhoengsoftseguro.artificio.usuario.Usuario;
import com.github.trabalhoengsoftseguro.artificio.usuario.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class ArtificioJpaTests {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ImagemBlobRepository imagemBlobRepository;

    @Autowired
    private ObraPortfolioRepository obraPortfolioRepository;

    @Autowired
    private TipoComissaoRepository tipoComissaoRepository;

    @Autowired
    private PedidoComissaoRepository pedidoComissaoRepository;

    @Autowired
    private AvaliacaoRepository avaliacaoRepository;

    @Test
    @DisplayName("Deve persistir e consultar entidades organizadas por feature com relacionamentos corretos")
    void testCicloPersistenciaEntidades() {
        // 1. Criar Artista e Cliente (Feature: usuario)
        Usuario artista = new Usuario("Rafael Artista", "rafael@artificio.local", "hash123", Role.ROLE_ARTISTA, "Ilustrador digital", "rafael@pix");
        artista = usuarioRepository.save(artista);

        Usuario cliente = new Usuario("Carla Cliente", "carla@artificio.local", "hash456", Role.ROLE_CLIENTE, "Entusiasta de arte", null);
        cliente = usuarioRepository.save(cliente);

        assertThat(artista.getId()).isNotNull();
        assertThat(artista.getId().version()).isEqualTo(7);
        assertThat(cliente.getId()).isNotNull();
        assertThat(cliente.getId().version()).isEqualTo(7);

        // 2. Salvar Imagem BLOB (Feature: imagem)
        byte[] dadosFake = "fake-image-bytes".getBytes();
        ImagemBlob imagem = new ImagemBlob("uuid-img-1", "obra1.png", "image/png", dadosFake);
        imagemBlobRepository.save(imagem);

        Optional<ImagemBlob> imgSalva = imagemBlobRepository.findById("uuid-img-1");
        assertThat(imgSalva).isPresent();
        assertThat(imgSalva.get().getDados()).isEqualTo(dadosFake);

        // 3. Salvar Obra no Portfólio (Feature: artista)
        ObraPortfolio obra = new ObraPortfolio("Cyber Samurai", "Arte conceitual", "Cyberpunk", "uuid-img-1", artista);
        obraPortfolioRepository.save(obra);
        assertThat(obraPortfolioRepository.findByArtistaIdOrderByDataCriacaoDesc(artista.getId())).hasSize(1);

        // 4. Salvar Tipo de Comissão (Feature: artista)
        TipoComissao tipo = new TipoComissao("Retrato Cyberpunk", "Ilustração de busto", "Cyberpunk", new BigDecimal("150.00"), 5, artista);
        tipoComissaoRepository.save(tipo);
        assertThat(tipoComissaoRepository.findByArtistaIdAndAtivoTrue(artista.getId())).hasSize(1);

        // 5. Salvar Pedido de Comissão (Feature: comissao)
        PedidoComissao pedido = new PedidoComissao("Quero um guerreiro samurai com máscara de neon", new BigDecimal("150.00"), 5, cliente, artista, tipo);
        pedido = pedidoComissaoRepository.save(pedido);
        assertThat(pedido.getStatus()).isEqualTo(StatusComissao.SOLICITADA);

        // 6. Transitar estado e salvar Avaliação (Feature: avaliacao)
        pedido.setStatus(StatusComissao.CONCLUIDA);
        pedidoComissaoRepository.save(pedido);

        Avaliacao avaliacao = new Avaliacao(5, "Trabalho impecável, muito rápido!", pedido, cliente, artista);
        avaliacaoRepository.save(avaliacao);

        Double media = avaliacaoRepository.calcularMediaNotasPorArtista(artista.getId());
        assertThat(media).isEqualTo(5.0);
    }
}
