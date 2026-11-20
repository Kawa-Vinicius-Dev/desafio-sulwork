package com.sulwork.service;

import com.sulwork.domain.Participante;
import com.sulwork.dto.ParticipanteResponse;
import com.sulwork.exception.BusinessRuleException;
import com.sulwork.exception.ResourceNotFoundException;
import com.sulwork.repository.ParticipanteRepository;
import com.sulwork.dto.ParticipanteRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ParticipanteService {

    private final ParticipanteRepository repository;

    public ParticipanteService(ParticipanteRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public ParticipanteResponse criar(ParticipanteRequest request) {
        String cpf = request.cpf().replaceAll("\\D", "");
        if (repository.existsByCpf(cpf)) {
            throw new BusinessRuleException("Já existe participante com esse CPF");
        }
        Participante p = new Participante();
        p.setNome(request.nome());
        p.setCpf(cpf);
        p.setRestricoesAlimentares(request.restricoesAlimentares());
        return ParticipanteResponse.from(repository.save(p));
    }

    @Transactional(readOnly = true)
    public List<ParticipanteResponse> listar() {
        return repository.findAll().stream().map(ParticipanteResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public ParticipanteResponse buscar(Long id) {
        return ParticipanteResponse.from(buscarEntidade(id));
    }

    Participante buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Participante", id));
    }
}
