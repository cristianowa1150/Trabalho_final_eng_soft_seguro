package com.github.trabalhoengsoftseguro.artificio.artista;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ObraPortfolioRepository extends JpaRepository<ObraPortfolio, UUID> {

    List<ObraPortfolio> findByArtistaIdOrderByDataCriacaoDesc(UUID artistaId);

    List<ObraPortfolio> findByCategoriaIgnoreCase(String categoria);

    long countByArtistaId(UUID artistaId);
}
