package com.github.trabalhoengsoftseguro.artificio.comissao;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

/**
 * DTO para submissão de nova proposta/solicitação de comissão por um cliente.
 */
public record SolicitacaoComissaoDto(
        @NotNull(message = "O tipo de comissão deve ser selecionado")
        UUID tipoComissaoId,

        @NotBlank(message = "O briefing detalhando seu pedido é obrigatório")
        @Size(min = 10, max = 4000, message = "O briefing deve conter entre 10 e 4000 caracteres")
        String briefing
) {
}
