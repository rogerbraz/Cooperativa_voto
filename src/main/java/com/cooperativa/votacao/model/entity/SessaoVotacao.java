package com.cooperativa.votacao.model.entity;

import com.cooperativa.votacao.model.enums.StatusSessao;
import jakarta.persistence.*;
import lombok.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Entity
@Table(name = "sessao_votacao")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SessaoVotacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pauta_id", nullable = false, unique = true)
    private Pauta pauta;

    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura;

    @Column(name = "data_fechamento", nullable = false)
    private LocalDateTime dataFechamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusSessao status;

    @Column(name = "criada_em", nullable = false, updatable = false)
    private LocalDateTime criadaEm;

    @PrePersist
    public void prePersist() {
        if (this.criadaEm == null) {
            this.criadaEm = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = StatusSessao.ABERTA;
        }
    }

    public boolean isAberta() {
        if (this.status != StatusSessao.ABERTA) {
            return false;
        }
        return LocalDateTime.now().isBefore(this.dataFechamento);
    }

    public boolean isExpirada(LocalDateTime now) {
        return now.isAfter(this.dataFechamento) || now.isEqual(this.dataFechamento);
    }

    public void encerrar() {
        this.status = StatusSessao.ENCERRADA;
    }

    public long getSegundosRestantes() {
        LocalDateTime now = LocalDateTime.now();
        if (!isAberta()) {
            return 0;
        }
        return Math.max(0, Duration.between(now, this.dataFechamento).toSeconds());
    }
}
