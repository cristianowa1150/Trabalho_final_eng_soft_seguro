package com.github.trabalhoengsoftseguro.artificio.avaliacao;

import com.github.trabalhoengsoftseguro.artificio.artista.TipoComissao;
import com.github.trabalhoengsoftseguro.artificio.comissao.PedidoComissao;
import com.github.trabalhoengsoftseguro.artificio.comissao.PedidoComissaoRepository;
import com.github.trabalhoengsoftseguro.artificio.comissao.StatusComissao;
import com.github.trabalhoengsoftseguro.artificio.usuario.Role;
import com.github.trabalhoengsoftseguro.artificio.usuario.Usuario;
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
class AvaliacaoServiceTest {

    @Mock
    private AvaliacaoRepository avaliacaoRepository;

    @Mock
    private PedidoComissaoRepository pedidoRepository;

    private AvaliacaoService service;

    private Usuario cliente;
    private Usuario artista;
    private PedidoComissao pedido;

    private final UUID clienteId = UUID.randomUUID();
    private final UUID artistaId = UUID.randomUUID();
    private final UUID outroUsuarioId = UUID.randomUUID();
    private final UUID tipoId = UUID.randomUUID();
    private final UUID pedidoId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new AvaliacaoService(avaliacaoRepository, pedidoRepository);

        cliente = new Usuario("Cliente", "cli@artificio.local", "hash", Role.ROLE_CLIENTE, null, null);
        cliente.setId(clienteId);

        artista = new Usuario("Artista", "art@artificio.local", "hash", Role.ROLE_ARTISTA, "Bio", "pix");
        artista.setId(artistaId);

        TipoComissao tipo = new TipoComissao("Arte", "Desc", "Cat", new BigDecimal("100.00"), 5, artista);
        tipo.setId(tipoId);

        pedido = new PedidoComissao("Briefing", new BigDecimal("100.00"), 5, cliente, artista, tipo);
        pedido.setId(pedidoId);
        pedido.setStatus(StatusComissao.CONCLUIDA);
    }

    @Test
    @DisplayName("Deve salvar avaliacao com sucesso e atualizar status do pedido para AVALIADA")
    void deveSalvarAvaliacaoComSucessoEAtualizarStatusPedido() {
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
        when(avaliacaoRepository.existsByPedidoComissaoId(pedidoId)).thenReturn(false);
        when(avaliacaoRepository.save(any(Avaliacao.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NovaAvaliacaoDto dto = new NovaAvaliacaoDto(pedidoId, 5, "Ficou perfeito!");
        Avaliacao salva = service.salvarAvaliacao(clienteId, dto);

        assertNotNull(salva);
        assertEquals(5, salva.getNota());
        assertEquals("Ficou perfeito!", salva.getComentario());
        assertEquals(StatusComissao.AVALIADA, pedido.getStatus());

        verify(avaliacaoRepository).save(any(Avaliacao.class));
        verify(pedidoRepository).save(pedido);
    }

    @Test
    @DisplayName("Deve rejeitar avaliacao se o pedido ainda nao estiver no estado CONCLUIDA")
    void deveRejeitarAvaliacaoParaPedidoNaoConcluido() {
        pedido.setStatus(StatusComissao.EM_ANDAMENTO);
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));

        NovaAvaliacaoDto dto = new NovaAvaliacaoDto(pedidoId, 5, "Tentando avaliar antes do tempo");

        assertThrows(IllegalStateException.class, () -> service.salvarAvaliacao(clienteId, dto));
        verify(avaliacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve rejeitar avaliacao duplicada para o mesmo pedido")
    void deveRejeitarAvaliacaoDuplicadaParaMesmoPedido() {
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));
        when(avaliacaoRepository.existsByPedidoComissaoId(pedidoId)).thenReturn(true);

        NovaAvaliacaoDto dto = new NovaAvaliacaoDto(pedidoId, 4, "Tentativa duplicada");

        assertThrows(IllegalStateException.class, () -> service.salvarAvaliacao(clienteId, dto));
        verify(avaliacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve rejeitar avaliacao enviada por outro usuario que nao seja o cliente do pedido")
    void deveRejeitarAvaliacaoPorUsuarioDiferenteDoCliente() {
        when(pedidoRepository.findById(pedidoId)).thenReturn(Optional.of(pedido));

        NovaAvaliacaoDto dto = new NovaAvaliacaoDto(pedidoId, 5, "Tentativa indevida");

        // Usuário com ID outroUsuarioId tentando avaliar pedido do cliente
        assertThrows(IllegalArgumentException.class, () -> service.salvarAvaliacao(outroUsuarioId, dto));
        verify(avaliacaoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve obter media de notas do artista com sucesso")
    void deveObterMediaDeNotasDoArtista() {
        when(avaliacaoRepository.calcularMediaNotasPorArtista(artistaId)).thenReturn(4.75);

        Double media = service.obterMediaArtista(artistaId);

        assertEquals(4.75, media);
        verify(avaliacaoRepository).calcularMediaNotasPorArtista(artistaId);
    }
}
