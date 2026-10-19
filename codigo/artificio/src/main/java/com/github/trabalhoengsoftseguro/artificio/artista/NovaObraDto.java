package com.github.trabalhoengsoftseguro.artificio.artista;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO para publicação de nova obra no portfólio.
 */
public record NovaObraDto(
        @NotBlank(message = "O título da obra é obrigatório")
        @Size(min = 2, max = 150, message = "O título deve ter entre 2 e 150 caracteres")
        String titulo,

        @Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres")
        String descricao,

        @NotBlank(message = "A categoria é obrigatória")
        @Size(min = 2, max = 80, message = "A categoria deve ter entre 2 e 80 caracteres")
        String categoria
) {
}
