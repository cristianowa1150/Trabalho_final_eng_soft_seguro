package com.github.trabalhoengsoftseguro.artificio.avaliacao;

import com.github.trabalhoengsoftseguro.artificio.comissao.PedidoComissao;
import com.github.trabalhoengsoftseguro.artificio.comissao.PedidoComissaoRepository;
import com.github.trabalhoengsoftseguro.artificio.comissao.StatusComissao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final PedidoComissaoRepository pedidoRepository;

    public AvaliacaoService(AvaliacaoRepository avaliacaoRepository, PedidoComissaoRepository pedidoRepository) {
        this.avaliacaoRepository = avaliacaoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    @Transactional
    public Avaliacao salvarAvaliacao(UUID clienteId, NovaAvaliacaoDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Dados da avaliação não podem ser nulos");
        }

        PedidoComissao pedido = pedidoRepository.findById(dto.pedidoId())
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado com ID: " + dto.pedidoId()));

        if (!pedido.getCliente().getId().equals(clienteId)) {
            throw new IllegalArgumentException("Apenas o cliente contratante pode avaliar este serviço");
        }

        if (pedido.getStatus() != StatusComissao.CONCLUIDA) {
            throw new IllegalStateException("Apenas pedidos concluídos e aprovados podem receber avaliação");
        }

        if (avaliacaoRepository.existsByPedidoComissaoId(dto.pedidoId())) {
            throw new IllegalStateException("Este pedido já foi avaliado anteriormente");
        }

        if (dto.nota() < 1 || dto.nota() > 5) {
            throw new IllegalArgumentException("A nota deve ser um valor inteiro entre 1 e 5");
        }

        Avaliacao avaliacao = new Avaliacao(
                dto.nota(),
                dto.comentario(),
                pedido,
                pedido.getCliente(),
                pedido.getArtista()
        );

        Avaliacao salva = avaliacaoRepository.save(avaliacao);

        pedido.setStatus(StatusComissao.AVALIADA);
        pedidoRepository.save(pedido);

        return salva;
    }

    public Double obterMediaArtista(UUID artistaId) {
        Double media = avaliacaoRepository.calcularMediaNotasPorArtista(artistaId);
        return media != null ? media : 0.0;
    }

    public long contarAvaliacoesArtista(UUID artistaId) {
        return avaliacaoRepository.countByArtistaId(artistaId);
    }

    public List<Avaliacao> listarPorArtista(UUID artistaId) {
        return avaliacaoRepository.findByArtistaIdOrderByDataAvaliacaoDesc(artistaId);
    }
}
