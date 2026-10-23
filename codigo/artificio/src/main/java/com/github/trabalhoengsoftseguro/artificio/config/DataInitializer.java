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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * Carga inicial de demonstração (seed) para o MVP da plataforma Artifício.
 * Configura integrantes, catálogo e cenários de ciclo de vida para auditoria forense.
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
    private final PlatformTransactionManager transactionManager;

    public DataInitializer(UsuarioRepository usuarioRepository,
                           PasswordEncoder passwordEncoder,
                           ImagemService imagemService,
                           ObraPortfolioRepository obraPortfolioRepository,
                           TipoComissaoRepository tipoComissaoRepository,
                           PedidoComissaoRepository pedidoComissaoRepository,
                           AvaliacaoRepository avaliacaoRepository,
                           PlatformTransactionManager transactionManager) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.imagemService = imagemService;
        this.obraPortfolioRepository = obraPortfolioRepository;
        this.tipoComissaoRepository = tipoComissaoRepository;
        this.pedidoComissaoRepository = pedidoComissaoRepository;
        this.avaliacaoRepository = avaliacaoRepository;
        this.transactionManager = transactionManager;
    }

    private void executarEmTransacaoComo(String email, Role role, Consumer<TransactionStatus> acao) {
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        if (email != null && role != null) {
            Authentication auth = new UsernamePasswordAuthenticationToken(email, "N/A", List.of(new SimpleGrantedAuthority(role.name())));
            context.setAuthentication(auth);
        }
        SecurityContextHolder.setContext(context);
        try {
            TransactionTemplate template = new TransactionTemplate(transactionManager);
            template.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
            template.executeWithoutResult(acao);
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Override
    public void run(String... args) {
        executarEmTransacaoComo(null, null, s -> inicializarUsuariosECatalogo());

        if (pedidoComissaoRepository.count() <= 1) {
            inicializarCenariosAuditoria();
        }
    }

    private void inicializarUsuariosECatalogo() {
        // 1. Artista 1 - Anna Letycia Fernandes Reis (Aquarela e Botânica)
        if (!usuarioRepository.existsByEmail("anna@artificio.local")) {
            Usuario anna = new Usuario(
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

            TipoComissao tcBotanica = new TipoComissao("Retrato Botânico Personalizado", "Pintura detalhada de flores, plantas e folhagens", "Aquarela", new BigDecimal("180.00"), 5, anna);
            tipoComissaoRepository.save(tcBotanica);

            TipoComissao tcPet = new TipoComissao("Retrato Aquarelado de Pet", "Seu cão ou gato imortalizado em aquarela estilizada", "Aquarela", new BigDecimal("150.00"), 4, anna);
            tipoComissaoRepository.save(tcPet);
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

        // 3. Artista 3 - Marina Duarte (Ilustração Vetorial)
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

        // 4. Clientes
        if (!usuarioRepository.existsByEmail("eduardo@artificio.local")) {
            Usuario eduardo = new Usuario(
                    "Eduardo Rolim",
                    "eduardo@artificio.local",
                    passwordEncoder.encode("Eduardo&Cliente2026"),
                    Role.ROLE_CLIENTE,
                    "Entusiasta de arte digital, desenvolvimento seguro e encomendas autorais personalizadas.",
                    null
            );
            usuarioRepository.save(eduardo);
        }

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
    }

    private void inicializarCenariosAuditoria() {
        Usuario anna = usuarioRepository.findByEmail("anna@artificio.local").orElse(null);
        Usuario cristiano = usuarioRepository.findByEmail("cristiano@artificio.local").orElse(null);
        Usuario marina = usuarioRepository.findByEmail("marina@artificio.local").orElse(null);
        Usuario eduardo = usuarioRepository.findByEmail("eduardo@artificio.local").orElse(null);
        Usuario luan = usuarioRepository.findByEmail("luan@artificio.local").orElse(null);

        if (anna == null || cristiano == null || eduardo == null || luan == null) {
            return;
        }

        TipoComissao tcBotanica = tipoComissaoRepository.findByArtistaId(anna.getId()).stream().findFirst().orElse(null);
        TipoComissao tcSprite = tipoComissaoRepository.findByArtistaId(cristiano.getId()).stream().findFirst().orElse(null);
        TipoComissao tcCenario = tipoComissaoRepository.findByArtistaId(cristiano.getId()).stream()
                .filter(tc -> tc.getTitulo().contains("Cenário")).findFirst().orElse(null);
        TipoComissao tcVetor = marina != null ? tipoComissaoRepository.findByArtistaId(marina.getId()).stream().findFirst().orElse(null) : null;

        // CENÁRIO 1: Encomenda Completa e Avaliada (Orquídeas Azuis)
        if (tcBotanica != null && pedidoComissaoRepository.count() == 0) {
            final UUID[] idPedidoHolder = new UUID[1];

            // 1.1 Eduardo solicita encomenda
            executarEmTransacaoComo("eduardo@artificio.local", Role.ROLE_CLIENTE, s -> {
                PedidoComissao p = new PedidoComissao(
                        "Quero um arranjo de orquídeas azuis com um beija-flor em voo.",
                        tcBotanica.getPrecoBase(),
                        tcBotanica.getPrazoEstimadoDias(),
                        eduardo,
                        anna,
                        tcBotanica
                );
                p.setStatus(StatusComissao.SOLICITADA);
                p = pedidoComissaoRepository.save(p);
                idPedidoHolder[0] = p.getId();
            });

            // 1.2 Anna aceita a encomenda
            executarEmTransacaoComo("anna@artificio.local", Role.ROLE_ARTISTA, s -> {
                PedidoComissao p = pedidoComissaoRepository.findById(idPedidoHolder[0]).orElseThrow();
                p.setStatus(StatusComissao.EM_ANDAMENTO);
                pedidoComissaoRepository.save(p);
            });

            // 1.3 Anna conclui a encomenda e entrega o link
            executarEmTransacaoComo("anna@artificio.local", Role.ROLE_ARTISTA, s -> {
                PedidoComissao p = pedidoComissaoRepository.findById(idPedidoHolder[0]).orElseThrow();
                p.setStatus(StatusComissao.CONCLUIDA);
                p.setLinkEntrega("https://drive.google.com/exemplo-arte-botanica-final.png");
                p.setDataConclusao(LocalDateTime.now().minusDays(1));
                pedidoComissaoRepository.save(p);
            });

            // 1.4 Eduardo avalia com nota 5
            executarEmTransacaoComo("eduardo@artificio.local", Role.ROLE_CLIENTE, s -> {
                PedidoComissao p = pedidoComissaoRepository.findById(idPedidoHolder[0]).orElseThrow();
                p.setStatus(StatusComissao.AVALIADA);
                pedidoComissaoRepository.save(p);

                Avaliacao avaliacao = new Avaliacao(
                        5,
                        "A arte ficou maravilhosa! Cores vivas, detalhes impressionantes e entrega antes do prazo.",
                        p,
                        eduardo,
                        anna
                );
                avaliacaoRepository.save(avaliacao);
            });
        }

        // CENÁRIO 2: Encomenda em Andamento (Sprite de Combate)
        if (tcSprite != null) {
            final UUID[] idHolder = new UUID[1];
            executarEmTransacaoComo("luan@artificio.local", Role.ROLE_CLIENTE, s -> {
                PedidoComissao p = new PedidoComissao(
                        "Sprite animado de cavaleiro medieval com espada de fogo (poses de combate).",
                        tcSprite.getPrecoBase(),
                        tcSprite.getPrazoEstimadoDias(),
                        luan,
                        cristiano,
                        tcSprite
                );
                p.setStatus(StatusComissao.SOLICITADA);
                p = pedidoComissaoRepository.save(p);
                idHolder[0] = p.getId();
            });

            executarEmTransacaoComo("cristiano@artificio.local", Role.ROLE_ARTISTA, s -> {
                PedidoComissao p = pedidoComissaoRepository.findById(idHolder[0]).orElseThrow();
                p.setStatus(StatusComissao.EM_ANDAMENTO);
                pedidoComissaoRepository.save(p);
            });
        }

        // CENÁRIO 3: Encomenda Pendente de Aceite (Capa Vetorial)
        if (tcVetor != null && marina != null) {
            executarEmTransacaoComo("eduardo@artificio.local", Role.ROLE_CLIENTE, s -> {
                PedidoComissao p = new PedidoComissao(
                        "Arte conceitual vetorial para capa de podcast sobre segurança da informação.",
                        tcVetor.getPrecoBase(),
                        tcVetor.getPrazoEstimadoDias(),
                        eduardo,
                        marina,
                        tcVetor
                );
                p.setStatus(StatusComissao.SOLICITADA);
                pedidoComissaoRepository.save(p);
            });
        }

        // CENÁRIO 4: Encomenda Recusada pelo Artista (Prazo Incompatível)
        if (tcBotanica != null) {
            final UUID[] idHolder = new UUID[1];
            executarEmTransacaoComo("luan@artificio.local", Role.ROLE_CLIENTE, s -> {
                PedidoComissao p = new PedidoComissao(
                        "Ilustração com urgência para 24h de mural botânico gigante.",
                        tcBotanica.getPrecoBase(),
                        1,
                        luan,
                        anna,
                        tcBotanica
                );
                p.setStatus(StatusComissao.SOLICITADA);
                p = pedidoComissaoRepository.save(p);
                idHolder[0] = p.getId();
            });

            executarEmTransacaoComo("anna@artificio.local", Role.ROLE_ARTISTA, s -> {
                PedidoComissao p = pedidoComissaoRepository.findById(idHolder[0]).orElseThrow();
                p.setStatus(StatusComissao.RECUSADA);
                pedidoComissaoRepository.save(p);
            });
        }

        // CENÁRIO 5: Encomenda Cancelada pelo Cliente
        if (tcCenario != null) {
            final UUID[] idHolder = new UUID[1];
            executarEmTransacaoComo("luan@artificio.local", Role.ROLE_CLIENTE, s -> {
                PedidoComissao p = new PedidoComissao(
                        "Cenário noturno futurista para background de live na Twitch.",
                        tcCenario.getPrecoBase(),
                        tcCenario.getPrazoEstimadoDias(),
                        luan,
                        cristiano,
                        tcCenario
                );
                p.setStatus(StatusComissao.SOLICITADA);
                p = pedidoComissaoRepository.save(p);
                idHolder[0] = p.getId();
            });

            executarEmTransacaoComo("luan@artificio.local", Role.ROLE_CLIENTE, s -> {
                PedidoComissao p = pedidoComissaoRepository.findById(idHolder[0]).orElseThrow();
                p.setStatus(StatusComissao.CANCELADA);
                pedidoComissaoRepository.save(p);
            });
        }

        // CENÁRIO 6: Auditoria de Usuário - Promoção de Privilégios por Administrador
        executarEmTransacaoComo(null, null, s -> {
            if (!usuarioRepository.existsByEmail("renato.ilustrador@artificio.local")) {
                Usuario renato = new Usuario(
                        "Renato Silva",
                        "renato.ilustrador@artificio.local",
                        passwordEncoder.encode("Renato#2026"),
                        Role.ROLE_CLIENTE,
                        "Designer aspirante a artista oficial da plataforma.",
                        null
                );
                usuarioRepository.save(renato);
            }
        });

        executarEmTransacaoComo("admin@artificio.local", Role.ROLE_ADMIN, s -> {
            usuarioRepository.findByEmail("renato.ilustrador@artificio.local").ifPresent(renato -> {
                if (renato.getRole() == Role.ROLE_CLIENTE) {
                    renato.setRole(Role.ROLE_ARTISTA);
                    usuarioRepository.save(renato);
                }
            });
        });
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
