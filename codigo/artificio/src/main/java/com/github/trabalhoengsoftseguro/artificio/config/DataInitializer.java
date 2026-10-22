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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Carga inicial de demonstração (seed) para o MVP da plataforma Artifício.
 * Configura os integrantes do trabalho como artistas e clientes, com senhas distintas.
 * Ativo exclusivamente em ambiente de desenvolvimento/avaliação (!prod).
 */
@Component
@Order(2)
@Profile("!prod")
@ConditionalOnProperty(name = "artificio.seed.enabled", havingValue = "true", matchIfMissing = true)
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
        // 1. Artista 1 - Anna Letycia Fernandes Reis (Aquarela e Botânica)
        Usuario anna;
        TipoComissao tcBotanica = null;
        Optional<Usuario> annaOpt = usuarioRepository.findByEmail("anna@artificio.local");
        if (annaOpt.isEmpty()) {
            anna = new Usuario(
                    "Anna Letycia Fernandes Reis",
                    "anna@artificio.local",
                    passwordEncoder.encode("Anna@Arte2026"),
                    Role.ROLE_ARTISTA,
                    "Ilustradora e artista visual. Especializada em pintura digital botânica, flora e aquarela estilizada.",
                    "anna@pix.local"
            );
            anna = usuarioRepository.save(anna);

            String svgFlor = gerarSvgDemo("Jardim de Magnólias", "#e87a90", "#2d3748");
            String imgFlorId = imagemService.salvar(svgFlor.getBytes(StandardCharsets.UTF_8), "magnolias.svg", "image/svg+xml");
            ObraPortfolio obraFlor = new ObraPortfolio("Jardim de Magnólias", "Pintura botânica tradicional digitalizada", "Aquarela", imgFlorId, anna);
            obraPortfolioRepository.save(obraFlor);

            String svgRaposa = gerarSvgDemo("Raposa da Floresta", "#dd6b20", "#1a202c");
            String imgRaposaId = imagemService.salvar(svgRaposa.getBytes(StandardCharsets.UTF_8), "raposa.svg", "image/svg+xml");
            ObraPortfolio obraRaposa = new ObraPortfolio("Raposa da Floresta", "Ilustração infantil aquarelada", "Aquarela", imgRaposaId, anna);
            obraPortfolioRepository.save(obraRaposa);

            tcBotanica = new TipoComissao("Retrato Botânico Personalizado", "Pintura detalhada de flores, plantas e folhagens", "Aquarela", new BigDecimal("180.00"), 5, anna);
            tcBotanica = tipoComissaoRepository.save(tcBotanica);

            TipoComissao tcPet = new TipoComissao("Retrato Aquarelado de Pet", "Seu cão ou gato imortalizado em aquarela estilizada", "Aquarela", new BigDecimal("150.00"), 4, anna);
            tipoComissaoRepository.save(tcPet);
        } else {
            anna = annaOpt.get();
            tcBotanica = tipoComissaoRepository.findByArtistaId(anna.getId()).stream().findFirst().orElse(null);
        }

        // 2. Artista 2 - Cristiano Silveira Silva (Pixel Art)
        if (!usuarioRepository.existsByEmail("cristiano@artificio.local")) {
            Usuario cristiano = new Usuario(
                    "Cristiano Silveira Silva",
                    "cristiano@artificio.local",
                    passwordEncoder.encode("Cris#Pixel2026"),
                    Role.ROLE_ARTISTA,
                    "Pixel artist e designer de jogos. Focado em retrogaming, estética cyberpunk e animação 16-bits.",
                    "cristiano@pix.local"
            );
            cristiano = usuarioRepository.save(cristiano);

            String svgCyber = gerarSvgDemo("Cyber City 2099", "#805ad5", "#0f172a");
            String imgCyberId = imagemService.salvar(svgCyber.getBytes(StandardCharsets.UTF_8), "cyber_city.svg", "image/svg+xml");
            ObraPortfolio obraCyber = new ObraPortfolio("Cyber City 2099", "Cenário noturno futurista em pixel art", "Pixel Art", imgCyberId, cristiano);
            obraPortfolioRepository.save(obraCyber);

            String svgKnight = gerarSvgDemo("Cavaleiro Rúnico", "#319795", "#111827");
            String imgKnightId = imagemService.salvar(svgKnight.getBytes(StandardCharsets.UTF_8), "cavaleiro.svg", "image/svg+xml");
            ObraPortfolio obraKnight = new ObraPortfolio("Cavaleiro Rúnico", "Sprite de combate animado em 16-bits", "Pixel Art", imgKnightId, cristiano);
            obraPortfolioRepository.save(obraKnight);

            TipoComissao tcSprite = new TipoComissao("Sprite de Personagem (32x32)", "Criação de spritesheets estáticos ou com poses básicas", "Pixel Art", new BigDecimal("95.00"), 3, cristiano);
            tipoComissaoRepository.save(tcSprite);

            TipoComissao tcCenario = new TipoComissao("Cenário Completo Pixel Art", "Background detalhado para streams, wallpapers ou jogos", "Pixel Art", new BigDecimal("260.00"), 7, cristiano);
            tipoComissaoRepository.save(tcCenario);
        }

        // 3. Artista 3 - Artista Extra: Marina Duarte (Ilustração Vetorial)
        if (!usuarioRepository.existsByEmail("marina@artificio.local")) {
            Usuario marina = new Usuario(
                    "Marina Duarte",
                    "marina@artificio.local",
                    passwordEncoder.encode("Marina*Vetor2026"),
                    Role.ROLE_ARTISTA,
                    "Ilustradora vetorial e designer de personagens para mídias digitais e capas autorais.",
                    "marina@pix.local"
            );
            marina = usuarioRepository.save(marina);

            String svgSolar = gerarSvgDemo("Guerreira Solar", "#d69e2e", "#171923");
            String imgSolarId = imagemService.salvar(svgSolar.getBytes(StandardCharsets.UTF_8), "guerreira_solar.svg", "image/svg+xml");
            ObraPortfolio obraSolar = new ObraPortfolio("Guerreira Solar", "Ilustração vetorial de personagem em alta definição", "Arte Vetorial", imgSolarId, marina);
            obraPortfolioRepository.save(obraSolar);

            TipoComissao tcVetor = new TipoComissao("Ilustração Vetorial de Personagem", "Arte vetorial limpa com linhas suaves e paleta vibrante", "Arte Vetorial", new BigDecimal("130.00"), 4, marina);
            tipoComissaoRepository.save(tcVetor);
        }

        // 4. Cliente 1 - Eduardo Rolim
        Usuario eduardo;
        Optional<Usuario> eduardoOpt = usuarioRepository.findByEmail("eduardo@artificio.local");
        if (eduardoOpt.isEmpty()) {
            eduardo = new Usuario(
                    "Eduardo Rolim",
                    "eduardo@artificio.local",
                    passwordEncoder.encode("Eduardo&Cliente2026"),
                    Role.ROLE_CLIENTE,
                    "Entusiasta de arte digital, desenvolvimento seguro e encomendas autorais personalizadas.",
                    null
            );
            eduardo = usuarioRepository.save(eduardo);
        } else {
            eduardo = eduardoOpt.get();
        }

        // 5. Cliente 2 - Luan Diniz
        if (!usuarioRepository.existsByEmail("luan@artificio.local")) {
            Usuario luan = new Usuario(
                    "Luan Diniz",
                    "luan@artificio.local",
                    passwordEncoder.encode("Luan$Cliente2026"),
                    Role.ROLE_CLIENTE,
                    "Colecionador de ilustrações autorais e apoiador de artistas independentes.",
                    null
            );
            usuarioRepository.save(luan);
        }

        // 6. Pedido concluído e avaliado para inicialização de métricas da vitrine
        if (pedidoComissaoRepository.count() == 0 && tcBotanica != null && eduardo != null && anna != null) {
            PedidoComissao pedidoExemplo = new PedidoComissao(
                    "Quero um arranjo de orquídeas azuis com um beija-flor em voo.",
                    tcBotanica.getPrecoBase(),
                    tcBotanica.getPrazoEstimadoDias(),
                    eduardo,
                    anna,
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
                    eduardo,
                    anna
            );
            avaliacaoRepository.save(avaliacaoExemplo);
        }
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
