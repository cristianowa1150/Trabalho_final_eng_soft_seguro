package com.github.trabalhoengsoftseguro.artificio.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenAtivacaoRepository extends JpaRepository<TokenAtivacao, UUID> {

    Optional<TokenAtivacao> findByToken(String token);

    void deleteByUsuario(Usuario usuario);
}
