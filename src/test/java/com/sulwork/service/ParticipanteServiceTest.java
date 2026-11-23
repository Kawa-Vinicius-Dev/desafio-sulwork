package com.sulwork.service;

import com.sulwork.domain.Participante;
import com.sulwork.dto.ParticipanteRequest;
import com.sulwork.dto.ParticipanteResponse;
import com.sulwork.exception.BusinessRuleException;
import com.sulwork.repository.ParticipanteRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParticipanteServiceTest {

    @Mock ParticipanteRepository repository;
    @InjectMocks ParticipanteService service;

    @Test
    void guardaOCpfSoComNumeros() {
        when(repository.existsByCpf("52998224725")).thenReturn(false);
        when(repository.save(any(Participante.class))).thenAnswer(inv -> inv.getArgument(0));

        ParticipanteResponse resposta = service.criar(new ParticipanteRequest("Ana", "529.982.247-25", "lactose"));

        assertThat(resposta.cpf()).isEqualTo("52998224725");
        assertThat(resposta.restricoesAlimentares()).isEqualTo("lactose");
    }

    @Test
    void naoCadastraCpfRepetidoMesmoComPontuacaoDiferente() {
        when(repository.existsByCpf("52998224725")).thenReturn(true);

        assertThatThrownBy(() -> service.criar(new ParticipanteRequest("Ana", "529.982.247-25", null)))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Já existe participante com esse CPF");
        verify(repository, never()).save(any());
    }
}
