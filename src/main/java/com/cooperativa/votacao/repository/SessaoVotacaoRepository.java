package com.cooperativa.votacao.repository;

import com.cooperativa.votacao.model.entity.SessaoVotacao;
import com.cooperativa.votacao.model.enums.StatusSessao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessaoVotacaoRepository extends JpaRepository<SessaoVotacao, Long> {

    Optional<SessaoVotacao> findByPautaId(Long pautaId);

    boolean existsByPautaId(Long pautaId);

    @Query("SELECT s FROM SessaoVotacao s JOIN FETCH s.pauta WHERE s.status = :status AND s.dataFechamento <= :now")
    List<SessaoVotacao> findSessoesParaEncerrar(@Param("status") StatusSessao status, @Param("now") LocalDateTime now);

    @Query("SELECT s FROM SessaoVotacao s JOIN FETCH s.pauta WHERE s.status = 'ABERTA' AND s.dataFechamento > :now ORDER BY s.dataFechamento ASC")
    List<SessaoVotacao> findSessoesAbertas(@Param("now") LocalDateTime now);
}
