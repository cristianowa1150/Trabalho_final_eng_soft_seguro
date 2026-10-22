package com.github.trabalhoengsoftseguro.artificio.avaliacao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AvaliacaoRepository extends JpaRepository<Avaliacao, UUID> {

    List<Avaliacao> findByArtistaIdOrderByDataAvaliacaoDesc(UUID artistaId);

    boolean existsByPedidoComissaoId(UUID pedidoComissaoId);

    @Query("SELECT COALESCE(AVG(a.nota), 0.0) FROM Avaliacao a WHERE a.artista.id = :artistaId")
    Double calcularMediaNotasPorArtista(@Param("artistaId") UUID artistaId);

    long countByArtistaId(UUID artistaId);
}
