package com.github.trabalhoengsoftseguro.artificio.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenAtivacaoRepository extends JpaRepository<TokenAtivacao, Long> {

    Optional<TokenAtivacao> findByToken(String token);

    void deleteByUsuario(Usuario usuario);
}
