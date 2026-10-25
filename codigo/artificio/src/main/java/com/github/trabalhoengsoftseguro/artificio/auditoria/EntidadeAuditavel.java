package com.github.trabalhoengsoftseguro.artificio.auditoria;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.envers.Audited;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Superclasse mapeada JPA para auditoria automática do ciclo de vida das entidades.
 * Implementa o padrão MappedSuperclass da Parte 11 do Roteiro de Engenharia de Software Seguro.
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Audited
public abstract class EntidadeAuditavel {

    @CreatedDate
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @LastModifiedDate
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @CreatedBy
    @Column(name = "criado_por", updatable = false, length = 150)
    private String criadoPor;

    @LastModifiedBy
    @Column(name = "atualizado_por", length = 150)
    private String atualizadoPor;

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataAtualizacao() {
        return dataAtualizacao;
    }

    public void setDataAtualizacao(LocalDateTime dataAtualizacao) {
        this.dataAtualizacao = dataAtualizacao;
    }

    public String getCriadoPor() {
        return criadoPor;
    }

    public void setCriadoPor(String criadoPor) {
        this.criadoPor = criadoPor;
    }

    public String getAtualizadoPor() {
        return atualizadoPor;
    }

    public void setAtualizadoPor(String atualizadoPor) {
        this.atualizadoPor = atualizadoPor;
    }
}
