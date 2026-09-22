package com.cooperativa.votacao.repository;

import com.cooperativa.votacao.model.entity.Pauta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PautaRepository extends JpaRepository<Pauta, Long> {

    @Query("SELECT p FROM Pauta p LEFT JOIN FETCH p.sessao ORDER BY p.id DESC")
    List<Pauta> findAllWithSessao();
}
