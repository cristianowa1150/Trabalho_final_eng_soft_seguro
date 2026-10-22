package com.github.trabalhoengsoftseguro.artificio.usuario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByRole(Role role);

    List<Usuario> findByRole(Role role);

    List<Usuario> findByRoleOrderByNomeAsc(Role role);

    @Query("SELECT u FROM Usuario u WHERE u.role = :role AND (" +
            "LOWER(u.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR " +
            "LOWER(u.bio) LIKE LOWER(CONCAT('%', :termo, '%')))")
    List<Usuario> buscarArtistasPorTermo(@Param("role") Role role, @Param("termo") String termo);

    @Query("SELECT DISTINCT u FROM Usuario u WHERE u.role = :role " +
            "AND (:termo IS NULL OR :termo = '' OR LOWER(u.nome) LIKE LOWER(CONCAT('%', :termo, '%')) OR LOWER(u.bio) LIKE LOWER(CONCAT('%', :termo, '%'))) " +
            "AND (:categoria IS NULL OR :categoria = '' OR " +
            "EXISTS (SELECT 1 FROM ObraPortfolio o WHERE o.artista = u AND LOWER(o.categoria) = LOWER(:categoria)) OR " +
            "EXISTS (SELECT 1 FROM TipoComissao t WHERE t.artista = u AND t.ativo = true AND LOWER(t.categoria) = LOWER(:categoria))) " +
            "ORDER BY u.nome ASC")
    List<Usuario> buscarArtistasFiltrados(@Param("role") Role role,
                                          @Param("categoria") String categoria,
                                          @Param("termo") String termo);
}
