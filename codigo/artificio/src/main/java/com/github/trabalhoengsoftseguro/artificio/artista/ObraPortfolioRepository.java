package com.github.trabalhoengsoftseguro.artificio.artista;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ObraPortfolioRepository extends JpaRepository<ObraPortfolio, UUID> {

    List<ObraPortfolio> findByArtistaIdOrderByDataCriacaoDesc(UUID artistaId);

    List<ObraPortfolio> findByCategoriaIgnoreCase(String categoria);

    long countByArtistaId(UUID artistaId);
}
