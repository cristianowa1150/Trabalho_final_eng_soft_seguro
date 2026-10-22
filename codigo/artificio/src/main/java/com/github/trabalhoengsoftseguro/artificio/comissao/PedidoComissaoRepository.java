package com.github.trabalhoengsoftseguro.artificio.comissao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PedidoComissaoRepository extends JpaRepository<PedidoComissao, UUID> {

    List<PedidoComissao> findByClienteIdOrderByDataSolicitacaoDesc(UUID clienteId);

    List<PedidoComissao> findByArtistaIdOrderByDataSolicitacaoDesc(UUID artistaId);

    List<PedidoComissao> findByArtistaIdAndStatus(UUID artistaId, StatusComissao status);

    List<PedidoComissao> findByClienteIdAndStatus(UUID clienteId, StatusComissao status);
}
