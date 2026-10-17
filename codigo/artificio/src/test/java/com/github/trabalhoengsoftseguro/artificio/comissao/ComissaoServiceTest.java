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

    @BeforeEach
    void setUp() {
        service = new ComissaoService(pedidoRepository, usuarioRepository, tipoComissaoRepository);

        cliente = new Usuario("Cliente Teste", "cliente@teste.com", "hash", Role.ROLE_CLIENTE, null, null);
        cliente.setId(10L);

        artista = new Usuario("Artista Teste", "artista@teste.com", "hash", Role.ROLE_ARTISTA, "Bio", "pix-key");
        artista.setId(20L);

        tipoComissao = new TipoComissao("Pixel Art", "Busto", "Pixel", new BigDecimal("120.00"), 4, artista);
        tipoComissao.setId(30L);
    }

    @Test
    @DisplayName("Deve criar solicitacao de comissao com preco base e prazo do tipo selecionado")
    void deveCriarSolicitacaoComSucesso() {
        when(usuarioRepository.findById(10L)).thenReturn(Optional.of(cliente));
        when(tipoComissaoRepository.findById(30L)).thenReturn(Optional.of(tipoComissao));
        when(pedidoRepository.save(any(PedidoComissao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        SolicitacaoComissaoDto dto = new SolicitacaoComissaoDto(30L, "Quero um avatar retro pixel art de 64x64");
        PedidoComissao criado = service.criarSolicitacao(10L, dto);

        assertNotNull(criado);
        assertEquals(StatusComissao.SOLICITADA, criado.getStatus());
        assertEquals(new BigDecimal("120.00"), criado.getValorFinal());
        assertEquals(4, criado.getPrazoDias());
        verify(pedidoRepository).save(any(PedidoComissao.class));
    }

    @Test
    @DisplayName("Deve rejeitar solicitacao se cliente tentar contratar seu proprio servico")
    void deveRejeitarSolicitacaoParaSiProprio() {
        when(usuarioRepository.findById(20L)).thenReturn(Optional.of(artista));
        when(tipoComissaoRepository.findById(30L)).thenReturn(Optional.of(tipoComissao));

        SolicitacaoComissaoDto dto = new SolicitacaoComissaoDto(30L, "Briefing valido");

        assertThrows(IllegalArgumentException.class, () -> service.criarSolicitacao(20L, dto));
        verify(pedidoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve aceitar pedido de comissao e gerar codigo PIX simulado")
    void deveAceitarPedidoComSucessoGerandoPix() {
        PedidoComissao pedido = new PedidoComissao("Briefing", new BigDecimal("120.00"), 4, cliente, artista, tipoComissao);
        pedido.setId(100L);

        when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(PedidoComissao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PedidoComissao aceito = service.aceitarPedido(100L, 20L);

        assertEquals(StatusComissao.AGUARDANDO_PAGAMENTO, aceito.getStatus());
        assertNotNull(aceito.getCodigoPixSimulado());
        assertTrue(aceito.getCodigoPixSimulado().contains("PIX"));
    }

    @Test
    @DisplayName("Deve impedir que outro artista aceite pedido destinado a terceiro")
    void deveRejeitarAceitePorArtistaDiferente() {
        PedidoComissao pedido = new PedidoComissao("Briefing", new BigDecimal("120.00"), 4, cliente, artista, tipoComissao);
        pedido.setId(100L);

        when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));

        // Artista com ID 99 tentando aceitar pedido destinado ao artista ID 20
        assertThrows(IllegalArgumentException.class, () -> service.aceitarPedido(100L, 99L));
        assertEquals(StatusComissao.SOLICITADA, pedido.getStatus());
    }

    @Test
    @DisplayName("Deve registrar pagamento simulado e transitar para PAGO")
    void deveRegistrarPagamentoSimulado() {
        PedidoComissao pedido = new PedidoComissao("Briefing", new BigDecimal("120.00"), 4, cliente, artista, tipoComissao);
        pedido.setId(100L);
        pedido.setStatus(StatusComissao.AGUARDANDO_PAGAMENTO);

        when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(PedidoComissao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PedidoComissao pago = service.registrarPagamentoSimulado(100L, 10L);

        assertEquals(StatusComissao.PAGO, pago.getStatus());
    }

    @Test
    @DisplayName("Deve registrar entrega de arquivo e permitir aprovacao pelo cliente")
    void deveRegistrarEntregaEAprovarConclusao() {
        PedidoComissao pedido = new PedidoComissao("Briefing", new BigDecimal("120.00"), 4, cliente, artista, tipoComissao);
        pedido.setId(100L);
        pedido.setStatus(StatusComissao.EM_ANDAMENTO);

        when(pedidoRepository.findById(100L)).thenReturn(Optional.of(pedido));
        when(pedidoRepository.save(any(PedidoComissao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Artista entrega
        PedidoComissao entregue = service.registrarEntrega(100L, "https://drive.com/arte-final.png", 20L);
        assertEquals(StatusComissao.ENTREGUE, entregue.getStatus());
        assertEquals("https://drive.com/arte-final.png", entregue.getLinkEntrega());

        // Cliente aprova
        PedidoComissao concluido = service.aprovarConclusao(100L, 10L);
        assertEquals(StatusComissao.CONCLUIDA, concluido.getStatus());
        assertNotNull(concluido.getDataConclusao());
    }
}
