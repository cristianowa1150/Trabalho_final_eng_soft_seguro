package com.github.trabalhoengsoftseguro.artificio.artista;

import java.util.UUID;

/**
 * DTO imutável com as informações públicas e não sensíveis do perfil de um artista.
 * Evita a exposição direta da entidade JPA Usuario na camada de apresentação.
 */
public record ArtistaResumoDto(
        UUID id,
        String nome,
        String bio
) {
}

