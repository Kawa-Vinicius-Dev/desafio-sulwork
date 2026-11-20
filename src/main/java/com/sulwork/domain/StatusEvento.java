package com.sulwork.domain;

import java.util.Set;

public enum StatusEvento{
    RASCUNHO, ABERTO, ENCERRADO, REALIZADO, CANCELADO;

    public boolean podeMudarPara(StatusEvento novo){
        return switch (this){
            case RASCUNHO -> Set.of(ABERTO, CANCELADO).contains(novo);
            case ABERTO -> Set.of(ENCERRADO, CANCELADO).contains(novo);
            case ENCERRADO -> Set.of(REALIZADO, CANCELADO).contains(novo);
            case CANCELADO, REALIZADO -> false;
        };
    }

}
