package com.cooperativa.votacao.model.entity;

import com.cooperativa.votacao.model.enums.OpcaoVoto;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "voto", uniqueConstraints = {
    @UniqueConstraint(name = "uk_voto_sessao_associado", columnNames = {"sessao_id", "cpf_associado"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sessao_id", nullable = false)
    private SessaoVotacao sessao;

    @Column(name = "cpf_associado", nullable = false, length = 14)
    private String cpfAssociado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private OpcaoVoto opcao;

    @Column(name = "data_hora", nullable = false, updatable = false)
    private LocalDateTime dataHora;

    @PrePersist
    public void prePersist() {
        if (this.dataHora == null) {
            this.dataHora = LocalDateTime.now();
        }
    }
}
