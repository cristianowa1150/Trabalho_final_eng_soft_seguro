package com.github.trabalhoengsoftseguro.artificio.config;

import com.github.trabalhoengsoftseguro.artificio.artista.ObraPortfolio;
import com.github.trabalhoengsoftseguro.artificio.artista.ObraPortfolioRepository;
import com.github.trabalhoengsoftseguro.artificio.artista.TipoComissao;
import com.github.trabalhoengsoftseguro.artificio.artista.TipoComissaoRepository;
import com.github.trabalhoengsoftseguro.artificio.avaliacao.Avaliacao;
import com.github.trabalhoengsoftseguro.artificio.avaliacao.AvaliacaoRepository;
import com.github.trabalhoengsoftseguro.artificio.comissao.PedidoComissao;
import com.github.trabalhoengsoftseguro.artificio.comissao.PedidoComissaoRepository;
import com.github.trabalhoengsoftseguro.artificio.comissao.StatusComissao;
import com.github.trabalhoengsoftseguro.artificio.imagem.ImagemService;
import com.github.trabalhoengsoftseguro.artificio.usuario.Role;
import com.github.trabalhoengsoftseguro.artificio.usuario.Usuario;
import com.github.trabalhoengsoftseguro.artificio.usuario.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

/**
 * Carga inicial de demonstração (seed) para o MVP da plataforma Artifício.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final ImagemService imagemService;
    private final ObraPortfolioRepository obraPortfolioRepository;
    private final TipoComissaoRepository tipoComissaoRepository;
    private final PedidoComissaoRepository pedidoComissaoRepository;
    private final AvaliacaoRepository avaliacaoRepository;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder,
                           ImagemService imagemService,
                           ObraPortfolioRepository obraPortfolioRepository,
                           TipoComissaoRepository tipoComissaoRepository,
                           PedidoComissaoRepository pedidoComissaoRepository,
                           AvaliacaoRepository avaliacaoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.imagemService = imagemService;
        this.obraPortfolioRepository = obraPortfolioRepository;
        this.tipoComissaoRepository = tipoComissaoRepository;
        this.pedidoComissaoRepository = pedidoComissaoRepository;
        this.avaliacaoRepository = avaliacaoRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.count() > 0) {
            return;
        }

        String senhaPadrao = passwordEncoder.encode("senha123");

        // 1. Administrador
        Usuario admin = new Usuario(
                "Administrador da Plataforma",
                "admin@artificio.local",
                senhaPadrao,
                Role.ROLE_ADMIN,
                "Equipe de moderação e governança da plataforma Artifício.",
                null
        );
        usuarioRepository.save(admin);

        // 2. Artista 1 - Beatriz Aquarela
        Usuario beatriz = new Usuario(
                "Beatriz Silveira",
                "beatriz@artificio.local",
                senhaPadrao,
                Role.ROLE_ARTISTA,
                "Ilustradora botânica e aquarista profissional há 6 anos. Especializada em flora e fauna.",
                "beatriz@pix.local"
        );
        beatriz = usuarioRepository.save(beatriz);

        String svgFlor = gerarSvgDemo("Jardim de Magnólias", "#e87a90", "#2d3748");
        String imgFlorId = imagemService.salvar(svgFlor.getBytes(StandardCharsets.UTF_8), "magnolias.svg", "image/svg+xml");
        ObraPortfolio obraFlor = new ObraPortfolio("Jardim de Magnólias", "Pintura botânica tradicional digitalizada", "Aquarela", imgFlorId, beatriz);
        obraPortfolioRepository.save(obraFlor);

        String svgRaposa = gerarSvgDemo("Raposa da Floresta", "#dd6b20", "#1a202c");
        String imgRaposaId = imagemService.salvar(svgRaposa.getBytes(StandardCharsets.UTF_8), "raposa.svg", "image/svg+xml");
        ObraPortfolio obraRaposa = new ObraPortfolio("Raposa da Floresta", "Ilustração infantil aquarelada", "Aquarela", imgRaposaId, beatriz);
        obraPortfolioRepository.save(obraRaposa);

        TipoComissao tcBotanica = new TipoComissao("Retrato Botânico Personalizado", "Pintura detalhada de flores, plantas e folhagens", "Aquarela", new BigDecimal("180.00"), 5, beatriz);
        tipoComissaoRepository.save(tcBotanica);

        TipoComissao tcPet = new TipoComissao("Retrato Aquarelado de Pet", "Seu cão ou gato imortalizado em aquarela estilizada", "Aquarela", new BigDecimal("150.00"), 4, beatriz);
        tipoComissaoRepository.save(tcPet);

        // 3. Artista 2 - Carlos Pixel
        Usuario carlos = new Usuario(
                "Carlos Pixel",
                "carlos@artificio.local",
                senhaPadrao,
                Role.ROLE_ARTISTA,
                "Pixel artist veterano focado em estética cyberpunk, retrogaming e assets para jogos indie.",
                "carlos@pix.local"
        );
        carlos = usuarioRepository.save(carlos);

        String svgCyber = gerarSvgDemo("Cyber City 2099", "#805ad5", "#0f172a");
        String imgCyberId = imagemService.salvar(svgCyber.getBytes(StandardCharsets.UTF_8), "cyber_city.svg", "image/svg+xml");
        ObraPortfolio obraCyber = new ObraPortfolio("Cyber City 2099", "Cenário noturno futurista em pixel art", "Pixel Art", imgCyberId, carlos);
        obraPortfolioRepository.save(obraCyber);

        String svgKnight = gerarSvgDemo("Cavaleiro Rúnico", "#319795", "#111827");
        String imgKnightId = imagemService.salvar(svgKnight.getBytes(StandardCharsets.UTF_8), "cavaleiro.svg", "image/svg+xml");
        ObraPortfolio obraKnight = new ObraPortfolio("Cavaleiro Rúnico", "Sprite de combate animado em 16-bits", "Pixel Art", imgKnightId, carlos);
        obraPortfolioRepository.save(obraKnight);

        TipoComissao tcSprite = new TipoComissao("Sprite de Personagem (32x32)", "Criação de spritesheets estáticos ou com poses básicas", "Pixel Art", new BigDecimal("95.00"), 3, carlos);
        tipoComissaoRepository.save(tcSprite);

        TipoComissao tcCenario = new TipoComissao("Cenário Completo Pixel Art", "Background detalhado para streams, wallpapers ou jogos", "Pixel Art", new BigDecimal("260.00"), 7, carlos);
        tipoComissaoRepository.save(tcCenario);

        // 4. Cliente - Lucas
        Usuario lucas = new Usuario(
                "Lucas Cliente",
                "lucas@artificio.local",
                senhaPadrao,
                Role.ROLE_CLIENTE,
                "Entusiasta de jogos e colecionador de ilustrações personalizadas.",
                null
        );
        lucas = usuarioRepository.save(lucas);

        // 5. Pedido concluído e com avaliação para inicialização de métricas da vitrine
        PedidoComissao pedidoExemplo = new PedidoComissao(
                "Quero um arranjo de orquídeas azuis com um beija-flor em voo.",
                tcBotanica.getPrecoBase(),
                tcBotanica.getPrazoEstimadoDias(),
                lucas,
                beatriz,
                tcBotanica
        );
        pedidoExemplo.setStatus(StatusComissao.AVALIADA);
        pedidoExemplo.setLinkEntrega("https://drive.google.com/exemplo-arte-botanica-final.png");
        pedidoExemplo.setDataConclusao(LocalDateTime.now().minusDays(1));
        pedidoExemplo = pedidoComissaoRepository.save(pedidoExemplo);

        Avaliacao avaliacaoExemplo = new Avaliacao(
                5,
                "A arte ficou maravilhosa! Cores vivas, detalhes impressionantes e entrega antes do prazo.",
                pedidoExemplo,
                lucas,
                beatriz
        );
        avaliacaoRepository.save(avaliacaoExemplo);
    }

    private String gerarSvgDemo(String titulo, String corDestaque, String corFundo) {
        return "<svg xmlns='http://www.w3.org/2000/svg' width='600' height='400' viewBox='0 0 600 400'>" +
                "<rect width='600' height='400' fill='" + corFundo + "'/>" +
                "<circle cx='300' cy='180' r='90' fill='" + corDestaque + "' opacity='0.85'/>" +
                "<text x='300' y='330' font-family='Arial, sans-serif' font-size='22' font-weight='bold' fill='#ffffff' text-anchor='middle'>" +
                titulo + "</text>" +
                "<text x='300' y='360' font-family='Arial, sans-serif' font-size='14' fill='#a0aec0' text-anchor='middle'>" +
                "Artifício - Obra de Portfólio (Demonstração)" + "</text>" +
                "</svg>";
    }
}
