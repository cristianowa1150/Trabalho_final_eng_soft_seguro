package com.github.trabalhoengsoftseguro.artificio.avaliacao;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO para submissão de nota e comentário avaliando o trabalho do artista.
 */
public record NovaAvaliacaoDto(
        @NotNull(message = "O ID do pedido é obrigatório")
        Long pedidoId,

        @NotNull(message = "A nota é obrigatória")
        @Min(value = 1, message = "A nota mínima é 1 estrela")
        @Max(value = 5, message = "A nota máxima é 5 estrelas")
        Integer nota,

        @Size(max = 1000, message = "O comentário deve ter no máximo 1000 caracteres")
        String comentario
) {
}
