package com.github.trabalhoengsoftseguro.artificio.artista;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TipoComissaoRepository extends JpaRepository<TipoComissao, UUID> {

    List<TipoComissao> findByArtistaIdAndAtivoTrue(UUID artistaId);

    List<TipoComissao> findByArtistaId(UUID artistaId);

    List<TipoComissao> findByCategoriaIgnoreCaseAndAtivoTrue(String categoria);
}
