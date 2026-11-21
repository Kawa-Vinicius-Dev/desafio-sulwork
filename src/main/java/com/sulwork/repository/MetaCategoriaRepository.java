package com.sulwork.repository;

import com.sulwork.domain.MetaCategoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MetaCategoriaRepository extends JpaRepository<MetaCategoria, Long> {

    List<MetaCategoria> findByEventoIdOrderByCategoriaNome(Long eventoId);

    Optional<MetaCategoria> findByEventoIdAndCategoriaId(Long eventoId, Long categoriaId);
}
