package com.sulwork.domain;

import java.util.Set;

public enum StatusEvento{
    RASCUNHO, ABERTO, INSCRICOES_ENCERRADAS, REALIZADO, CANCELADO;

    public boolean podeMudarPara(StatusEvento novo){
        return switch (this){
            case RASCUNHO -> Set.of(ABERTO, CANCELADO).contains(novo);
            case ABERTO -> Set.of(INSCRICOES_ENCERRADAS, CANCELADO).contains(novo);
            case INSCRICOES_ENCERRADAS -> Set.of(REALIZADO, CANCELADO).contains(novo);
            case CANCELADO, REALIZADO -> false;
        };
    }

}
