package com.github.trabalhoengsoftseguro.artificio.comissao;

import com.github.trabalhoengsoftseguro.artificio.artista.TipoComissao;
import com.github.trabalhoengsoftseguro.artificio.artista.TipoComissaoRepository;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComissaoServiceTest {

    @Mock
    private PedidoComissaoRepository pedidoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TipoComissaoRepository tipoComissaoRepository;

    private ComissaoService service;

    private Usuario cliente;
    private Usuario artista;
    private TipoComissao tipoComissao;

    private final UUID clienteId = UUID.randomUUID();
    private final UUID artistaId = UUID.randomUUID();
    private final UUID tipoComissaoId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new ComissaoService(pedidoRepository, usuarioRepository, tipoComissaoRepository);

        cliente = new Usuario("Cliente Teste", "cliente@teste.com", "hash", Role.ROLE_CLIENTE, null, null);
        cliente.setId(clienteId);

        artista = new Usuario("Artista Teste", "artista@teste.com", "hash", Role.ROLE_ARTISTA, "Bio", "pix-key");
        artista.setId(artistaId);

        tipoComissao = new TipoComissao("Pixel Art", "Busto", "Pixel", new BigDecimal("120.00"), 4, artista);
        tipoComissao.setId(tipoComissaoId);
    }

    @Test
    @DisplayName("Deve criar solicitacao de comissao com preco base e prazo do tipo selecionado")
    void deveCriarSolicitacaoComSucesso() {
        when(usuarioRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(tipoComissaoRepository.findById(tipoComissaoId)).thenReturn(Optional.of(tipoComissao));
        when(pedidoRepository.save(any(PedidoComissao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SolicitacaoComissaoDto dto = new SolicitacaoComissaoDto(tipoComissaoId, "Quero um avatar retro pixel art de 64x64");
        PedidoComissao criado = service.criarSolicitacao(clienteId, dto);

        assertNotNull(criado);
        assertEquals(StatusComissao.SOLICITADA, criado.getStatus());
        assertEquals(new BigDecimal("120.00"), criado.getValorFinal());
        assertEquals(4, criado.getPrazoDias());
        verify(pedidoRepository).save(any(PedidoComissao.class));
    }

    @Test
    @DisplayName("Deve rejeitar solicitacao se cliente tentar contratar seu proprio servico")
    void deveRejeitarSolicitacaoParaSiProprio() {
        when(usuarioRepository.findById(artistaId)).thenReturn(Optional.of(artista));
        when(tipoComissaoRepository.findById(tipoComissaoId)).thenReturn(Optional.of(tipoComissao));

        SolicitacaoComissaoDto dto = new SolicitacaoComissaoDto(tipoComissaoId, "Briefing valido");

        assertThrows(IllegalArgumentException.class, () -> service.criarSolicitacao(artistaId, dto));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve rejeitar solicitacao se solicitante for administrador")
    void deveRejeitarSolicitacaoQuandoClienteForAdministrador() {
        UUID adminId = UUID.randomUUID();
        Usuario admin = new Usuario("Admin", "admin@teste.com", "hash", Role.ROLE_ADMIN, null, null);
        admin.setId(adminId);
        when(usuarioRepository.findById(adminId)).thenReturn(Optional.of(admin));

        SolicitacaoComissaoDto dto = new SolicitacaoComissaoDto(tipoComissaoId, "Briefing valido");

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> service.criarSolicitacao(adminId, dto));
        assertTrue(ex.getMessage().contains("Contas administrativas"));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve aceitar pedido de comissao e gerar codigo PIX simulado")
    void deveAceitarPedidoComSucessoGerandoPix() {
        UUID pedidoId = UUID.randomUUID();
        PedidoComissao pedido = new PedidoComissao("Briefing", new BigDecimal("120.00"), 4, cliente, artista, tipoComissao);
        pedido.setId(pedidoId);

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(PedidoComissao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PedidoComissao aceito = service.aceitarPedido(pedidoId, artistaId);

        assertEquals(StatusComissao.AGUARDANDO_PAGAMENTO, aceito.getStatus());
        assertNotNull(aceito.getCodigoPixSimulado());
        assertTrue(aceito.getCodigoPixSimulado().contains("PIX"));
    }

    @Test
    @DisplayName("Deve impedir que outro artista aceite pedido destinado a terceiro")
    void deveRejeitarAceitePorArtistaDiferente() {
        UUID pedidoId = UUID.randomUUID();
        UUID outroArtistaId = UUID.randomUUID();
        PedidoComissao pedido = new PedidoComissao("Briefing", new BigDecimal("120.00"), 4, cliente, artista, tipoComissao);
        pedido.setId(pedidoId);

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));

        // Artista com outro ID tentando aceitar pedido destinado ao artista
        assertThrows(IllegalArgumentException.class, () -> service.aceitarPedido(pedidoId, outroArtistaId));
        assertEquals(StatusComissao.SOLICITADA, pedido.getStatus());
    }

    @Test
    @DisplayName("Deve registrar pagamento simulado e transitar para PAGO")
    void deveRegistrarPagamentoSimulado() {
        UUID pedidoId = UUID.randomUUID();
        PedidoComissao pedido = new PedidoComissao("Briefing", new BigDecimal("120.00"), 4, cliente, artista, tipoComissao);
        pedido.setId(pedidoId);
        pedido.setStatus(StatusComissao.AGUARDANDO_PAGAMENTO);

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(PedidoComissao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PedidoComissao pago = service.registrarPagamentoSimulado(pedidoId, clienteId);

        assertEquals(StatusComissao.PAGO, pago.getStatus());
    }

    @Test
    @DisplayName("Deve registrar entrega de arquivo e permitir aprovacao pelo cliente")
    void deveRegistrarEntregaEAprovarConclusao() {
        UUID pedidoId = UUID.randomUUID();
        PedidoComissao pedido = new PedidoComissao("Briefing", new BigDecimal("120.00"), 4, cliente, artista, tipoComissao);
        pedido.setId(pedidoId);
        pedido.setStatus(StatusComissao.EM_ANDAMENTO);

        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(PedidoComissao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Artista entrega
        PedidoComissao entregue = service.registrarEntrega(pedidoId, "https://drive.com/arte-final.png", artistaId);
        assertEquals(StatusComissao.ENTREGUE, entregue.getStatus());
        assertEquals("https://drive.com/arte-final.png", entregue.getLinkEntrega());

        // Cliente aprova
        PedidoComissao concluido = service.aprovarConclusao(pedidoId, clienteId);
        assertEquals(StatusComissao.CONCLUIDA, concluido.getStatus());
        assertNotNull(concluido.getDataConclusao());
    }
}
