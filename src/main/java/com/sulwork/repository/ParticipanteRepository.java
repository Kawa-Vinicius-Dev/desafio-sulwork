package com.sulwork.repository;

import com.sulwork.domain.Participante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParticipanteRepository extends JpaRepository<Participante, Long> {
    boolean existsByCpf(String cpf);
}
