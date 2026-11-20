package com.sulwork.repository;

public interface ItemFaltanteView {
    Long getItemId();
    String getNome();
    String getCategoriaNome();
    Integer getQuantidadeNecessaria();
    Long getQuantidadeConfirmada();
    Boolean getObrigatorio();
}