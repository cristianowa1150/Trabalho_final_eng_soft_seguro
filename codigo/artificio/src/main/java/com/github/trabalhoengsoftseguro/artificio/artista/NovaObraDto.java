package com.github.trabalhoengsoftseguro.artificio.artista;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para publicação de nova obra no portfólio.
 */
public record NovaObraDto(
        @NotBlank(message = "O título da obra é obrigatório")
        @Size(max = 150, message = "O título deve ter no máximo 150 caracteres")
        String titulo,

        String descricao,

        @NotBlank(message = "A categoria é obrigatória")
        String categoria
) {
}
