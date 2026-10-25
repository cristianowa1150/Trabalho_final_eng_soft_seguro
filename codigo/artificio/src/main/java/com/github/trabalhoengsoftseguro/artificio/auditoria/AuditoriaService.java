package com.github.trabalhoengsoftseguro.artificio.auditoria;

import com.github.trabalhoengsoftseguro.artificio.artista.ObraPortfolio;
import com.github.trabalhoengsoftseguro.artificio.artista.TipoComissao;
import com.github.trabalhoengsoftseguro.artificio.avaliacao.Avaliacao;
import com.github.trabalhoengsoftseguro.artificio.comissao.PedidoComissao;
import com.github.trabalhoengsoftseguro.artificio.usuario.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.RevisionType;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

/**
 * Serviço responsável por consultar e consolidar a trilha de auditoria forense do Hibernate Envers.
 */
@Service
@Transactional(readOnly = true)
public class AuditoriaService {

    @PersistenceContext
    private EntityManager entityManager;

    public Page<RegistroAuditoriaDto> listarRevisoesPaginadas(int pagina, int tamanho) {
        return listarRevisoesPaginadas(pagina, tamanho, null);
    }

    public Page<RegistroAuditoriaDto> listarRevisoesPaginadas(int pagina, int tamanho, String termoBusca) {
        List<RegistroAuditoriaDto> todos = listarUltimasRevisoes(500);

        if (termoBusca != null && !termoBusca.isBlank()) {
            String termo = termoBusca.trim().toLowerCase();
            todos = todos.stream()
                    .filter(r -> (r.entidade() != null && r.entidade().toLowerCase().contains(termo))
                            || (r.autor() != null && r.autor().toLowerCase().contains(termo))
                            || (r.operacao() != null && r.operacao().toLowerCase().contains(termo))
                            || (r.identificador() != null && r.identificador().toLowerCase().contains(termo))
                            || (r.resumo() != null && r.resumo().toLowerCase().contains(termo))
                            || (String.valueOf(r.numeroRevisao()).contains(termo)))
                    .toList();
        }

        int total = todos.size();
        int safePage = Math.max(0, pagina);
        int safeSize = Math.max(1, tamanho);
        long offset = (long) safePage * safeSize;
        int inicio = (offset > total) ? total : (int) offset;
        int fim = (int) Math.min((long) inicio + safeSize, (long) total);
        List<RegistroAuditoriaDto> sublista = todos.subList(inicio, fim);
        Pageable pageable = PageRequest.of(safePage, safeSize);
        return new PageImpl<>(sublista, pageable, total);
    }

    public List<RegistroAuditoriaDto> listarUltimasRevisoes(int limite) {
        AuditReader reader = AuditReaderFactory.get(entityManager);
        List<RegistroAuditoriaDto> registros = new ArrayList<>();

        adicionarRevisoes(reader, PedidoComissao.class, "Pedido de Comissão", registros);
        adicionarRevisoes(reader, Usuario.class, "Usuário", registros);
        adicionarRevisoes(reader, Avaliacao.class, "Avaliação", registros);
        adicionarRevisoes(reader, TipoComissao.class, "Tipo de Comissão", registros);
        adicionarRevisoes(reader, ObraPortfolio.class, "Obra do Portfólio", registros);

        return registros.stream()
                .sorted((r1, r2) -> Long.compare(r2.numeroRevisao(), r1.numeroRevisao()))
                .limit(limite)
                .toList();
    }

    @SuppressWarnings("unchecked")
    private <T> void adicionarRevisoes(AuditReader reader, Class<T> entityClass, String nomeAmigavel, List<RegistroAuditoriaDto> registros) {
        try {
            List<Object[]> resultados = reader.createQuery()
                    .forRevisionsOfEntity(entityClass, false, true)
                    .addOrder(AuditEntity.revisionNumber().desc())
                    .setMaxResults(30)
                    .getResultList();

            for (Object[] row : resultados) {
                T entidade = (T) row[0];
                DefaultRevisionEntity revEntity = (DefaultRevisionEntity) row[1];
                RevisionType revType = (RevisionType) row[2];

                Long revId = (long) revEntity.getId();
                LocalDateTime dataHora = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(revEntity.getTimestamp()),
                        ZoneId.systemDefault()
                );

                String operacao;
                String badgeClass;
                switch (revType) {
                    case ADD -> {
                        operacao = "CRIAÇÃO";
                        badgeClass = "badge-aceita";
                    }
                    case MOD -> {
                        operacao = "MODIFICAÇÃO";
                        badgeClass = "badge-em_andamento";
                    }
                    case DEL -> {
                        operacao = "EXCLUSÃO";
                        badgeClass = "badge-recusada";
                    }
                    default -> {
                        operacao = revType.name();
                        badgeClass = "badge-solicitada";
                    }
                }

                String autor = "SISTEMA";
                String identificador = "-";
                String resumo = "-";

                if (entidade instanceof EntidadeAuditavel auditavel) {
                    if (auditavel.getAtualizadoPor() != null) {
                        autor = auditavel.getAtualizadoPor();
                    } else if (auditavel.getCriadoPor() != null) {
                        autor = auditavel.getCriadoPor();
                    }
                }

                if (entidade instanceof PedidoComissao pedido) {
                    identificador = pedido.getId() != null ? pedido.getId().toString() : "-";
                    resumo = "Status: " + pedido.getStatus() + " | Valor: R$ " + pedido.getValorFinal() +
                             (pedido.getCliente() != null ? " | Cliente: " + pedido.getCliente().getNome() : "");
                } else if (entidade instanceof Usuario user) {
                    identificador = user.getId() != null ? user.getId().toString() : "-";
                    resumo = user.getNome() + " (" + user.getEmail() + ") - " + user.getRole();
                } else if (entidade instanceof Avaliacao avaliacao) {
                    identificador = avaliacao.getId() != null ? avaliacao.getId().toString() : "-";
                    resumo = "Nota: " + avaliacao.getNota() + "⭐ | Comentário: " + avaliacao.getComentario();
                } else if (entidade instanceof TipoComissao tipo) {
                    identificador = tipo.getId() != null ? tipo.getId().toString() : "-";
                    resumo = tipo.getTitulo() + " | R$ " + tipo.getPrecoBase() + (tipo.isAtivo() ? " (Ativo)" : " (Pausado)");
                } else if (entidade instanceof ObraPortfolio obra) {
                    identificador = obra.getId() != null ? obra.getId().toString() : "-";
                    resumo = obra.getTitulo() + " (" + obra.getCategoria() + ")";
                }

                registros.add(new RegistroAuditoriaDto(
                        revId,
                        dataHora,
                        nomeAmigavel,
                        entityClass.getSimpleName(),
                        operacao,
                        badgeClass,
                        autor,
                        identificador,
                        resumo
                ));
            }
        } catch (Exception ignored) {
            // Tratamento defensivo caso o banco ainda não possua revisões da entidade
        }
    }
}
