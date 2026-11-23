package com.sulwork.domain;

import org.junit.jupiter.api.Test;

import static com.sulwork.domain.StatusEvento.*;
import static org.assertj.core.api.Assertions.assertThat;

class StatusEventoTest {

    @Test
    void segueOFluxoNormal() {
        assertThat(RASCUNHO.podeMudarPara(ABERTO)).isTrue();
        assertThat(ABERTO.podeMudarPara(ENCERRADO)).isTrue();
        assertThat(ENCERRADO.podeMudarPara(REALIZADO)).isTrue();
    }

    @Test
    void podeCancelarAntesDeRealizar() {
        assertThat(RASCUNHO.podeMudarPara(CANCELADO)).isTrue();
        assertThat(ABERTO.podeMudarPara(CANCELADO)).isTrue();
        assertThat(ENCERRADO.podeMudarPara(CANCELADO)).isTrue();
    }

    @Test
    void naoPulaEtapaNemVoltaAtras() {
        assertThat(RASCUNHO.podeMudarPara(ENCERRADO)).isFalse();
        assertThat(ABERTO.podeMudarPara(REALIZADO)).isFalse();
        assertThat(ABERTO.podeMudarPara(RASCUNHO)).isFalse();
        assertThat(ENCERRADO.podeMudarPara(ABERTO)).isFalse();
    }

    @Test
    void realizadoECanceladoSaoFinais() {
        for (StatusEvento novo : StatusEvento.values()) {
            assertThat(REALIZADO.podeMudarPara(novo)).isFalse();
            assertThat(CANCELADO.podeMudarPara(novo)).isFalse();
        }
    }
}
