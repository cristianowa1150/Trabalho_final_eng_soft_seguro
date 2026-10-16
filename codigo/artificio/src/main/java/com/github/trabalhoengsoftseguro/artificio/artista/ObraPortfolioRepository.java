package com.github.trabalhoengsoftseguro.artificio.artista;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ObraPortfolioRepository extends JpaRepository<ObraPortfolio, Long> {

    List<ObraPortfolio> findByArtistaIdOrderByDataCriacaoDesc(Long artistaId);

    List<ObraPortfolio> findByCategoriaIgnoreCase(String categoria);

    long countByArtistaId(Long artistaId);
}
