package com.github.trabalhoengsoftseguro.artificio.comissao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoComissaoRepository extends JpaRepository<PedidoComissao, Long> {

    List<PedidoComissao> findByClienteIdOrderByDataSolicitacaoDesc(Long clienteId);

    List<PedidoComissao> findByArtistaIdOrderByDataSolicitacaoDesc(Long artistaId);

    List<PedidoComissao> findByArtistaIdAndStatus(Long artistaId, StatusComissao status);

    List<PedidoComissao> findByClienteIdAndStatus(Long clienteId, StatusComissao status);
}
