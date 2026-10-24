package com.github.trabalhoengsoftseguro.artificio.comissao;

import com.github.trabalhoengsoftseguro.artificio.artista.TipoComissao;
import com.github.trabalhoengsoftseguro.artificio.auditoria.EntidadeAuditavel;
import com.github.trabalhoengsoftseguro.artificio.usuario.Usuario;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.envers.Audited;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Entidade que gerencia os pedidos de comissão e suas transições de estado.
 */
@Entity
@Table(name = "pedidos_comissao")
@Audited
public class PedidoComissao extends EntidadeAuditavel {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;

    @Column(nullable = false, length = 4000)
    private String briefing;

    @Column(name = "valor_final", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorFinal;

    @Column(name = "prazo_dias", nullable = false)
    private Integer prazoDias;

    @Column(name = "link_entrega", length = 1000)
    private String linkEntrega;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 35)
    private StatusComissao status;

    @Version
    private Long versao;

    @Column(name = "codigo_pix_simulado", length = 255)
    private String codigoPixSimulado;

    @Column(name = "data_solicitacao", nullable = false)
    private LocalDateTime dataSolicitacao;

    @Column(name = "data_conclusao")
    private LocalDateTime dataConclusao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Usuario cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artista_id", nullable = false)
    private Usuario artista;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_comissao_id", nullable = false)
    private TipoComissao tipoComissao;

    public PedidoComissao() {
    }

    public PedidoComissao(String briefing, BigDecimal valorFinal, Integer prazoDias, Usuario cliente, Usuario artista, TipoComissao tipoComissao) {
        this.briefing = briefing;
        this.valorFinal = valorFinal;
        this.prazoDias = prazoDias;
        this.cliente = cliente;
        this.artista = artista;
        this.tipoComissao = tipoComissao;
        this.status = StatusComissao.SOLICITADA;
        this.dataSolicitacao = LocalDateTime.now();
    }

    @PrePersist
    public void prePersist() {
        if (this.dataSolicitacao == null) {
            this.dataSolicitacao = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = StatusComissao.SOLICITADA;
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getBriefing() {
        return briefing;
    }

    public void setBriefing(String briefing) {
        this.briefing = briefing;
    }

    public BigDecimal getValorFinal() {
        return valorFinal;
    }

    public void setValorFinal(BigDecimal valorFinal) {
        this.valorFinal = valorFinal;
    }

    public Integer getPrazoDias() {
        return prazoDias;
    }

    public void setPrazoDias(Integer prazoDias) {
        this.prazoDias = prazoDias;
    }

    public String getLinkEntrega() {
        return linkEntrega;
    }

    public void setLinkEntrega(String linkEntrega) {
        this.linkEntrega = linkEntrega;
    }

    public StatusComissao getStatus() {
        return status;
    }

    public void setStatus(StatusComissao status) {
        this.status = status;
    }

    public String getCodigoPixSimulado() {
        return codigoPixSimulado;
    }

    public void setCodigoPixSimulado(String codigoPixSimulado) {
        this.codigoPixSimulado = codigoPixSimulado;
    }

    public LocalDateTime getDataSolicitacao() {
        return dataSolicitacao;
    }

    public void setDataSolicitacao(LocalDateTime dataSolicitacao) {
        this.dataSolicitacao = dataSolicitacao;
    }

    public LocalDateTime getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(LocalDateTime dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public Usuario getCliente() {
        return cliente;
    }

    public void setCliente(Usuario cliente) {
        this.cliente = cliente;
    }

    public Usuario getArtista() {
        return artista;
    }

    public void setArtista(Usuario artista) {
        this.artista = artista;
    }

    public TipoComissao getTipoComissao() {
        return tipoComissao;
    }

    public void setTipoComissao(TipoComissao tipoComissao) {
        this.tipoComissao = tipoComissao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PedidoComissao that = (PedidoComissao) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
