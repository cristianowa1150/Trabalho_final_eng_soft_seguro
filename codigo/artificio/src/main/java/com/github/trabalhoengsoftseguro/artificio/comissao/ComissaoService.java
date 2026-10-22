package com.github.trabalhoengsoftseguro.artificio.comissao;

import com.github.trabalhoengsoftseguro.artificio.artista.TipoComissao;
import com.github.trabalhoengsoftseguro.artificio.artista.TipoComissaoRepository;
import com.github.trabalhoengsoftseguro.artificio.usuario.Usuario;
import com.github.trabalhoengsoftseguro.artificio.usuario.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ComissaoService {

    private final PedidoComissaoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final TipoComissaoRepository tipoComissaoRepository;

    public ComissaoService(PedidoComissaoRepository pedidoRepository,
                           UsuarioRepository usuarioRepository,
                           TipoComissaoRepository tipoComissaoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.usuarioRepository = usuarioRepository;
        this.tipoComissaoRepository = tipoComissaoRepository;
    }

    @Transactional
    public PedidoComissao criarSolicitacao(UUID clienteId, SolicitacaoComissaoDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Dados da solicitação não podem ser nulos");
        }

        Usuario cliente = usuarioRepository.findById(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado com ID: " + clienteId));

        if (cliente.isAdmin()) {
            throw new IllegalArgumentException("Contas administrativas não têm permissão para contratar comissões");
        }

        TipoComissao tipo = tipoComissaoRepository.findById(dto.tipoComissaoId())
                .orElseThrow(() -> new IllegalArgumentException("Tipo de comissão não encontrado com ID: " + dto.tipoComissaoId()));

        if (!tipo.isAtivo()) {
            throw new IllegalArgumentException("Esta modalidade de serviço não está mais disponível para novas encomendas");
        }

        Usuario artista = tipo.getArtista();
        if (artista.getId().equals(clienteId)) {
            throw new IllegalArgumentException("Você não pode solicitar uma comissão para si próprio");
        }

        PedidoComissao pedido = new PedidoComissao(
                dto.briefing(),
                tipo.getPrecoBase(),
                tipo.getPrazoEstimadoDias(),
                cliente,
                artista,
                tipo
        );

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public PedidoComissao aceitarPedido(UUID pedidoId, UUID artistaId) {
        PedidoComissao pedido = buscarPorId(pedidoId);

        if (!pedido.getArtista().getId().equals(artistaId)) {
            throw new IllegalArgumentException("Apenas o artista destinatário pode aceitar este pedido");
        }
        if (pedido.getStatus() != StatusComissao.SOLICITADA) {
            throw new IllegalStateException("Apenas pedidos em estado SOLICITADA podem ser aceitos");
        }

        pedido.setStatus(StatusComissao.AGUARDANDO_PAGAMENTO);
        pedido.setCodigoPixSimulado("00020126580014BR.GOV.BCB.PIX0136" + UUID.randomUUID().toString().replace("-", ""));

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public PedidoComissao recusarPedido(UUID pedidoId, UUID artistaId) {
        PedidoComissao pedido = buscarPorId(pedidoId);

        if (!pedido.getArtista().getId().equals(artistaId)) {
            throw new IllegalArgumentException("Apenas o artista destinatário pode recusar este pedido");
        }
        if (pedido.getStatus() != StatusComissao.SOLICITADA) {
            throw new IllegalStateException("Apenas pedidos em estado SOLICITADA podem ser recusados");
        }

        pedido.setStatus(StatusComissao.RECUSADA);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public PedidoComissao cancelarPedido(UUID pedidoId, UUID clienteId) {
        PedidoComissao pedido = buscarPorId(pedidoId);

        if (!pedido.getCliente().getId().equals(clienteId)) {
            throw new IllegalArgumentException("Apenas o cliente solicitante pode cancelar este pedido");
        }
        if (pedido.getStatus() != StatusComissao.SOLICITADA && pedido.getStatus() != StatusComissao.AGUARDANDO_PAGAMENTO) {
            throw new IllegalStateException("O pedido não pode ser cancelado após o início da produção ou pagamento");
        }

        pedido.setStatus(StatusComissao.CANCELADA);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public PedidoComissao registrarPagamentoSimulado(UUID pedidoId, UUID clienteId) {
        PedidoComissao pedido = buscarPorId(pedidoId);

        if (!pedido.getCliente().getId().equals(clienteId)) {
            throw new IllegalArgumentException("Apenas o cliente solicitante pode confirmar o pagamento");
        }
        if (pedido.getStatus() != StatusComissao.AGUARDANDO_PAGAMENTO) {
            throw new IllegalStateException("O pedido não está aguardando pagamento");
        }

        pedido.setStatus(StatusComissao.PAGO);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public PedidoComissao iniciarProducao(UUID pedidoId, UUID artistaId) {
        PedidoComissao pedido = buscarPorId(pedidoId);

        if (!pedido.getArtista().getId().equals(artistaId)) {
            throw new IllegalArgumentException("Apenas o artista pode iniciar a produção do trabalho");
        }
        if (pedido.getStatus() != StatusComissao.PAGO) {
            throw new IllegalStateException("Apenas pedidos pagos podem ter a produção iniciada");
        }

        pedido.setStatus(StatusComissao.EM_ANDAMENTO);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public PedidoComissao registrarEntrega(UUID pedidoId, String linkEntrega, UUID artistaId) {
        if (linkEntrega == null || linkEntrega.isBlank()) {
            throw new IllegalArgumentException("O link ou endereço do arquivo de entrega é obrigatório");
        }
        if (linkEntrega.trim().length() > 1000) {
            throw new IllegalArgumentException("O link de entrega deve conter no máximo 1000 caracteres");
        }

        PedidoComissao pedido = buscarPorId(pedidoId);

        if (!pedido.getArtista().getId().equals(artistaId)) {
            throw new IllegalArgumentException("Apenas o artista pode registrar a entrega do trabalho");
        }
        if (pedido.getStatus() != StatusComissao.EM_ANDAMENTO && pedido.getStatus() != StatusComissao.PAGO) {
            throw new IllegalStateException("O pedido deve estar em andamento para receber uma entrega");
        }

        pedido.setLinkEntrega(linkEntrega.trim());
        pedido.setStatus(StatusComissao.ENTREGUE);

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public PedidoComissao solicitarRevisao(UUID pedidoId, UUID clienteId) {
        PedidoComissao pedido = buscarPorId(pedidoId);

        if (!pedido.getCliente().getId().equals(clienteId)) {
            throw new IllegalArgumentException("Apenas o cliente pode solicitar revisão da arte entregue");
        }
        if (pedido.getStatus() != StatusComissao.ENTREGUE) {
            throw new IllegalStateException("Apenas pedidos entregues podem ter revisão solicitada");
        }

        pedido.setStatus(StatusComissao.EM_ANDAMENTO);
        return pedidoRepository.save(pedido);
    }

    @Transactional
    public PedidoComissao aprovarConclusao(UUID pedidoId, UUID clienteId) {
        PedidoComissao pedido = buscarPorId(pedidoId);

        if (!pedido.getCliente().getId().equals(clienteId)) {
            throw new IllegalArgumentException("Apenas o cliente pode aprovar a conclusão da comissão");
        }
        if (pedido.getStatus() != StatusComissao.ENTREGUE) {
            throw new IllegalStateException("Apenas pedidos com arte entregue podem ser concluídos e aprovados");
        }

        pedido.setStatus(StatusComissao.CONCLUIDA);
        pedido.setDataConclusao(LocalDateTime.now());

        return pedidoRepository.save(pedido);
    }

    public PedidoComissao buscarPorId(UUID pedidoId) {
        return pedidoRepository.findById(pedidoId)
                .orElseThrow(() -> new IllegalArgumentException("Pedido de comissão não encontrado com ID: " + pedidoId));
    }

    public List<PedidoComissao> listarPorCliente(UUID clienteId) {
        return pedidoRepository.findByClienteIdOrderByDataSolicitacaoDesc(clienteId);
    }

    public List<PedidoComissao> listarPorArtista(UUID artistaId) {
        return pedidoRepository.findByArtistaIdOrderByDataSolicitacaoDesc(artistaId);
    }
}
