import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { describe, expect, it, vi } from 'vitest';
import type { Evento, Participacao, Participante } from '../../api/types';
import type { Resource } from '../../hooks/useResource';
import { ParticipacoesSection } from './ParticipacoesSection';

function recurso<T>(data: T): Resource<T> {
  return { data, error: null, loading: false, reload: vi.fn() };
}

const evento: Evento = {
  id: 1,
  nome: 'Café de sexta',
  descricao: null,
  tipo: 'CAFE_DA_MANHA',
  dataHora: '2099-12-05T08:00:00',
  prazoAlteracoes: '2099-12-04T18:00:00',
  status: 'ABERTO',
};

const ana: Participante = { id: 2, nome: 'Ana', cpf: '52998224725', restricoesAlimentares: 'lactose' };
const bruno: Participante = { id: 3, nome: 'Bruno', cpf: '11144477735', restricoesAlimentares: null };

const inscricoes: Participacao[] = [
  { id: 10, eventoId: 1, participanteId: 2, participanteNome: 'Ana', inscritoEm: '2099-12-01T10:00:00' },
  { id: 11, eventoId: 1, participanteId: 3, participanteNome: 'Bruno', inscritoEm: '2099-12-01T11:00:00' },
];

describe('ParticipacoesSection', () => {
  it('mostra a restrição alimentar de quem tem', () => {
    render(
      <MemoryRouter>
        <ParticipacoesSection
          evento={evento}
          participacoes={recurso(inscricoes)}
          participantes={recurso([ana, bruno])}
          onInscrito={vi.fn()}
        />
      </MemoryRouter>,
    );

    expect(screen.getByText('Restrição: lactose')).toBeInTheDocument();
    expect(screen.getAllByText(/^Restrição:/)).toHaveLength(1);
  });
});
