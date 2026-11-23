package com.sulwork.repository;

import com.sulwork.domain.Item;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByEventoId(Long eventoId);

    /**
     * Busca o item travando a linha no banco (SELECT ... FOR UPDATE) até o fim da transação.
     * Assim duas contribuições no mesmo item ao mesmo tempo não passam da quantidade necessária:
     * a segunda espera a primeira terminar e já soma a quantidade dela.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT i FROM Item i WHERE i.id = :id")
    Optional<Item> buscarComLock(@Param("id") Long id);
}
