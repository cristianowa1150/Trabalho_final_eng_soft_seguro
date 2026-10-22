package com.github.trabalhoengsoftseguro.artificio.artista;

import com.github.trabalhoengsoftseguro.artificio.usuario.Usuario;
import java.util.List;

/**
 * DTO que agrega a visão completa do perfil público de um artista.
 */
public record ArtistaPerfilDto(
        Usuario artista,
        List<ObraPortfolio> obras,
        List<TipoComissao> tiposComissao,
        Double mediaNotas,
        long totalAvaliacoes
) {
}
