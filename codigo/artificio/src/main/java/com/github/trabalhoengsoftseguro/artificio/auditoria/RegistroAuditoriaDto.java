package com.github.trabalhoengsoftseguro.artificio.auditoria;

import java.time.LocalDateTime;

/**
 * DTO que consolida informações forenses de uma revisão do Hibernate Envers para visualização no painel administrativo.
 */
public record RegistroAuditoriaDto(
        Long numeroRevisao,
        LocalDateTime dataHora,
        String entidade,
        String entidadeNomeClasse,
        String operacao,
        String operacaoBadgeClass,
        String autor,
        String identificador,
        String resumo
) {}
