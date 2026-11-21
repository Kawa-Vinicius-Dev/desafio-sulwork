package com.sulwork.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Meta de cobertura de uma categoria dentro de um evento.
 * Ex.: no "Café de sexta", Bebidas precisa chegar a 100% e Doces a 50%.
 * A categoria é global; a meta é que pertence ao evento.
 */
@Entity
@Table(name = "meta_categoria",
        uniqueConstraints = @UniqueConstraint(name = "uk_meta_evento_categoria",
                columnNames = {"evento_id", "categoria_id"}))
@Getter
@Setter
@NoArgsConstructor
public class MetaCategoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id")
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    /** Percentual mínimo de cobertura que a categoria precisa atingir (1 a 100). */
    @Column(nullable = false)
    private Integer percentual;
}
