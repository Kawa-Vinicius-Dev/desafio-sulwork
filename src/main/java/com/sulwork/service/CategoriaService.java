package com.sulwork.service;

import com.sulwork.domain.Categoria;
import com.sulwork.dto.CategoriaRequest;
import com.sulwork.dto.CategoriaResponse;
import com.sulwork.exception.BusinessRuleException;
import com.sulwork.exception.ResourceNotFoundException;
import com.sulwork.repository.CategoriaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository repository;

    public CategoriaService(CategoriaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public CategoriaResponse criar(CategoriaRequest request) {
        String nome = request.nome().trim();
        if (repository.existsByNomeIgnoreCase(nome)) {
            throw new BusinessRuleException("Já existe categoria com esse nome");
        }
        Categoria c = new Categoria();
        c.setNome(nome);
        return
        CategoriaResponse.from(repository.save(c));
    }

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar(){
        return
                repository.findAll().stream().map(CategoriaResponse:: from).toList();
    }
    Categoria buscarEntidade(Long id){
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Categoria", id));
    }

}
