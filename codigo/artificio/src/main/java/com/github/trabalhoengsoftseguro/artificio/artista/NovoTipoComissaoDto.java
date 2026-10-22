package com.github.trabalhoengsoftseguro.artificio.artista;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/**
 * DTO para cadastro de novas modalidades de comissão por um artista.
 */
public record NovoTipoComissaoDto(
        @NotBlank(message = "O título é obrigatório")
        @Size(min = 2, max = 150, message = "O título deve ter entre 2 e 150 caracteres")
        String titulo,

        @Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres")
        String descricao,

        @NotBlank(message = "A categoria é obrigatória")
        @Size(min = 2, max = 80, message = "A categoria deve ter entre 2 e 80 caracteres")
        String categoria,

        @NotNull(message = "O preço base é obrigatório")
        @DecimalMin(value = "0.01", message = "O preço base deve ser no mínimo R$ 0,01")
        @DecimalMax(value = "99999999.99", message = "O preço base não pode exceder R$ 99.999.999,99")
        BigDecimal precoBase,

        @NotNull(message = "O prazo estimado é obrigatório")
        @Min(value = 1, message = "O prazo deve ser de pelo menos 1 dia")
        @Max(value = 365, message = "O prazo não pode exceder 365 dias")
        Integer prazoEstimadoDias
) {
}
