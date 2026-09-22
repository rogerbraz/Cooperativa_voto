package com.cooperativa.votacao.service;

import com.cooperativa.votacao.exception.PautaNaoEncontradaException;
import com.cooperativa.votacao.model.entity.Pauta;
import com.cooperativa.votacao.repository.PautaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PautaService {

    private final PautaRepository pautaRepository;

    @Transactional
    public Pauta cadastrarPauta(String titulo, String descricao) {
        log.info("Cadastrando nova pauta com título: {}", titulo);
        Pauta pauta = Pauta.builder()
                .titulo(titulo.trim())
                .descricao(descricao != null ? descricao.trim() : null)
                .build();
        return pautaRepository.save(pauta);
    }

    @Transactional(readOnly = true)
    public Pauta buscarPorId(Long id) {
        return pautaRepository.findById(id)
                .orElseThrow(() -> new PautaNaoEncontradaException(id));
    }

    @Transactional(readOnly = true)
    public List<Pauta> listarTodas() {
        return pautaRepository.findAllWithSessao();
    }
}
