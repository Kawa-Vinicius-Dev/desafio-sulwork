package com.sulwork.repository;

import com.sulwork.domain.Contribuicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ContribuicaoRepository extends JpaRepository<Contribuicao, Long> {

    List<Contribuicao> findByItemEventoId(Long eventoId);

    /** Busca a contribuição garantindo que ela pertence ao evento da URL. */
    Optional<Contribuicao> findByIdAndItemEventoId(Long id, Long eventoId);

    /** Total já confirmado para um item (0 se ninguém contribuiu). */
    @Query(value = """
            SELECT COALESCE(SUM(quantidade), 0)
              FROM contribuicao
             WHERE item_id = :itemId
            """, nativeQuery = true)
    long somarQuantidadePorItem(@Param("itemId") Long itemId);

    /** Total do item ignorando uma contribuição: usado ao alterar, pra não contar a quantidade antiga. */
    @Query(value = """
            SELECT COALESCE(SUM(quantidade), 0)
              FROM contribuicao
             WHERE item_id = :itemId
               AND id <> :contribuicaoId
            """, nativeQuery = true)
    long somarQuantidadePorItemExceto(@Param("itemId") Long itemId,
                                      @Param("contribuicaoId") Long contribuicaoId);

    /** Itens do evento que ainda não atingiram a quantidade necessária. */
    @Query(value = """
            SELECT i.id                            AS "itemId",
                   i.nome                          AS "nome",
                   c.nome                          AS "categoriaNome",
                   i.quantidade_necessaria         AS "quantidadeNecessaria",
                   COALESCE(SUM(ct.quantidade), 0) AS "quantidadeConfirmada",
                   i.obrigatorio                   AS "obrigatorio"
              FROM item i
              JOIN categoria c          ON c.id = i.categoria_id
              LEFT JOIN contribuicao ct ON ct.item_id = i.id
             WHERE i.evento_id = :eventoId
             GROUP BY i.id, i.nome, i.quantidade_necessaria, i.obrigatorio, c.nome
            HAVING COALESCE(SUM(ct.quantidade), 0) < i.quantidade_necessaria
             ORDER BY i.obrigatorio DESC, i.nome
            """, nativeQuery = true)
    List<ItemFaltanteView> buscarFaltantes(@Param("eventoId") Long eventoId);
}
