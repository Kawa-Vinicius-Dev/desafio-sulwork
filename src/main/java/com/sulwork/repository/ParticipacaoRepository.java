package com.sulwork.repository;

import com.sulwork.domain.Participacao;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipacaoRepository extends JpaRepository<Participacao, Long> {

    boolean existsByEventoIdAndParticipanteId(Long eventoId, Long participanteId);

    List<Participacao> findByEventoId(Long eventoId);
}