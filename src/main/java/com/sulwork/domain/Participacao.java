package com.sulwork.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "participacao",
        uniqueConstraints = @UniqueConstraint(columnNames = {"evento_id", "participante_id"}))
@Getter
@Setter
@NoArgsConstructor
public class Participacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "evento_id")
    private Evento evento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participante_id")
    private Participante participante;

    @Column(name = "inscrito_em", nullable = false)
    private LocalDateTime inscritoEm = LocalDateTime.now();
}