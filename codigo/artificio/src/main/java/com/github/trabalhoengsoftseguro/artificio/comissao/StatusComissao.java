package com.github.trabalhoengsoftseguro.artificio.comissao;

/**
 * Estados do ciclo de vida de uma encomenda/comissão artística.
 */
public enum StatusComissao {
    SOLICITADA,
    RECUSADA,
    CANCELADA,
    AGUARDANDO_PAGAMENTO,
    PAGO,
    EM_ANDAMENTO,
    ENTREGUE,
    CONCLUIDA,
    AVALIADA
}
