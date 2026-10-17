package com.github.trabalhoengsoftseguro.artificio.artista;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TipoComissaoRepository extends JpaRepository<TipoComissao, Long> {

    List<TipoComissao> findByArtistaIdAndAtivoTrue(Long artistaId);

    List<TipoComissao> findByArtistaId(Long artistaId);

    List<TipoComissao> findByCategoriaIgnoreCaseAndAtivoTrue(String categoria);
}
