package com.github.trabalhoengsoftseguro.artificio.artista;

import java.util.List;

/**
 * DTO que agrega a visão completa do perfil público de um artista.
 * Utiliza o DTO ArtistaResumoDto e coleções imutáveis para prevenir exposição indevida de representação interna.
 */
public record ArtistaPerfilDto(
        ArtistaResumoDto artista,
        List<ObraPortfolio> obras,
        List<TipoComissao> tiposComissao,
        Double mediaNotas,
        long totalAvaliacoes
) {
    public ArtistaPerfilDto {
        obras = obras != null ? List.copyOf(obras) : List.of();
        tiposComissao = tiposComissao != null ? List.copyOf(tiposComissao) : List.of();
    }
}
