package com.github.trabalhoengsoftseguro.artificio.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findByRole(Role role);

    List<Usuario> findByRoleOrderByNomeAsc(Role role);

    @Query("SELECT u FROM Usuario u WHERE u.role = :role AND (" +
            "LOWER(u.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
            "LOWER(u.bio) LIKE LOWER(CONCAT('%', :termo, '%')))")
    List<Usuario> buscarArtistasPorTermo(@Param("role") Role role, @Param("termo") String termo);
}
