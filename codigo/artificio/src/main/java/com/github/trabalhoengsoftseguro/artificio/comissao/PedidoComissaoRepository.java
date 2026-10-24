package com.github.trabalhoengsoftseguro.artificio.comissao;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PedidoComissaoRepository extends JpaRepository<PedidoComissao, UUID> {

    List<PedidoComissao> findByClienteIdOrderByDataSolicitacaoDesc(UUID clienteId);

    List<PedidoComissao> findByArtistaIdOrderByDataSolicitacaoDesc(UUID artistaId);

    List<PedidoComissao> findByArtistaIdAndStatus(UUID artistaId, StatusComissao status);

    List<PedidoComissao> findByClienteIdAndStatus(UUID clienteId, StatusComissao status);
}
