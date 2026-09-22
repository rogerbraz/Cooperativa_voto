package com.cooperativa.votacao.repository;

import com.cooperativa.votacao.model.entity.Voto;
import com.cooperativa.votacao.model.enums.OpcaoVoto;
import com.cooperativa.votacao.repository.dto.VotoContagemProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VotoRepository extends JpaRepository<Voto, Long> {

    boolean existsBySessaoIdAndCpfAssociado(Long sessaoId, String cpfAssociado);

    @Query("SELECT EXISTS (SELECT 1 FROM Voto v WHERE v.sessao.pauta.id = :pautaId AND v.cpfAssociado = :cpf)")
    boolean existsByPautaIdAndCpfAssociado(@Param("pautaId") Long pautaId, @Param("cpf") String cpf);

    long countBySessaoId(Long sessaoId);

    long countBySessaoIdAndOpcao(Long sessaoId, OpcaoVoto opcao);

    @Query("SELECT v.opcao as opcao, COUNT(v) as total FROM Voto v WHERE v.sessao.id = :sessaoId GROUP BY v.opcao")
    List<VotoContagemProjection> obterContagemAgregadaPorSessao(@Param("sessaoId") Long sessaoId);
}
