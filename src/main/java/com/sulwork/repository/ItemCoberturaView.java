package com.sulwork.repository;

/** Uma linha por item do evento, com o total já confirmado. Usada no cálculo de cobertura. */
public interface ItemCoberturaView {
    Long getItemId();
    Long getCategoriaId();
    String getCategoriaNome();
    Integer getQuantidadeNecessaria();
    Long getQuantidadeConfirmada();
    Boolean getObrigatorio();
}
