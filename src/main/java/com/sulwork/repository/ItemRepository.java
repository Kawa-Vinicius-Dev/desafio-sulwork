package com.sulwork.repository;

import com.sulwork.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByEventoId(Long eventoId);
}