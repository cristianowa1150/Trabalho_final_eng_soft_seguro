package com.github.trabalhoengsoftseguro.artificio.artista;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

/**
 * DTO para cadastro de novas modalidades de comissão por um artista.
 */
public record NovoTipoComissaoDto(
        @NotBlank(message = "O título é obrigatório")
        String titulo,

        String descricao,

        @NotBlank(message = "A categoria é obrigatória")
        String categoria,

        @NotNull(message = "O preço base é obrigatório")
        @PositiveOrZero(message = "O preço base não pode ser negativo")
        BigDecimal precoBase,

        @NotNull(message = "O prazo estimado é obrigatório")
        @Min(value = 1, message = "O prazo deve ser de pelo menos 1 dia")
        Integer prazoEstimadoDias
) {
}
