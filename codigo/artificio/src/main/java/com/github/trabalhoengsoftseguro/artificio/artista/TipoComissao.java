package com.github.trabalhoengsoftseguro.artificio.artista;

import com.github.trabalhoengsoftseguro.artificio.usuario.Usuario;
import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Define um tipo/modalidade de comissão oferecida por um artista (ex: Retrato Digital, Pixel Art, Chibi).
 */
@Entity
@Table(name = "tipos_comissao")
public class TipoComissao {

    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.VERSION_7)
    private UUID id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(length = 2000)
    private String descricao;

    @Column(nullable = false, length = 80)
    private String categoria;

    @Column(name = "preco_base", nullable = false, precision = 10, scale = 2)
    private BigDecimal precoBase;

    @Column(name = "prazo_estimado_dias", nullable = false)
    private Integer prazoEstimadoDias;

    @Column(nullable = false)
    private boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artista_id", nullable = false)
    private Usuario artista;

    public TipoComissao() {
    }

    public TipoComissao(String titulo, String descricao, String categoria, BigDecimal precoBase, Integer prazoEstimadoDias, Usuario artista) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.categoria = categoria;
        this.precoBase = precoBase;
        this.prazoEstimadoDias = prazoEstimadoDias;
        this.artista = artista;
        this.ativo = true;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public BigDecimal getPrecoBase() {
        return precoBase;
    }

    public void setPrecoBase(BigDecimal precoBase) {
        this.precoBase = precoBase;
    }

    public Integer getPrazoEstimadoDias() {
        return prazoEstimadoDias;
    }

    public void setPrazoEstimadoDias(Integer prazoEstimadoDias) {
        this.prazoEstimadoDias = prazoEstimadoDias;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Usuario getArtista() {
        return artista;
    }

    public void setArtista(Usuario artista) {
        this.artista = artista;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TipoComissao that = (TipoComissao) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
