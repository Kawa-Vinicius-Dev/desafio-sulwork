import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import type { Contribuicao, Evento } from '../../api/types';
import type { Resource } from '../../hooks/useResource';
import { mockFetch } from '../../test/fetchMock';
import { ContribuicoesSection } from './ContribuicoesSection';

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

const contribuicao: Contribuicao = {
  id: 7,
  itemId: 3,
  itemNome: 'Pão',
  participanteId: 2,
  participanteNome: 'Ana',
  quantidade: 2,
  criadoEm: '2099-12-01T10:00:00',
};

function renderizar(ev: Evento, onContribuido = vi.fn()) {
  render(
    <ContribuicoesSection
      evento={ev}
      itens={recurso([])}
      participacoes={recurso([])}
      contribuicoes={recurso([contribuicao])}
      onContribuido={onContribuido}
    />,
  );
  return onContribuido;
}

describe('ContribuicoesSection', () => {
  it('altera a quantidade com PUT e recarrega a lista', async () => {
    const fetch = mockFetch([
      { method: 'PUT', path: '/eventos/1/contribuicoes/7', body: { ...contribuicao, quantidade: 3 } },
    ]);
    const onContribuido = renderizar(evento);

    await userEvent.click(screen.getByRole('button', { name: 'Alterar' }));
    const campo = screen.getByLabelText('Nova quantidade de Pão');
    await userEvent.clear(campo);
    await userEvent.type(campo, '3');
    await userEvent.click(screen.getByRole('button', { name: 'Salvar' }));

    expect(fetch.mock.calls[0][1]?.body).toBe('{"quantidade":3}');
    expect(onContribuido).toHaveBeenCalled();
  });

  it('remove com DELETE depois de confirmar', async () => {
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    mockFetch([{ method: 'DELETE', path: '/eventos/1/contribuicoes/7', status: 204 }]);
    const onContribuido = renderizar(evento);

    await userEvent.click(screen.getByRole('button', { name: 'Remover' }));

    expect(onContribuido).toHaveBeenCalled();
  });

  it('mostra o erro da API quando a alteração viola uma regra', async () => {
    mockFetch([
      {
        method: 'PUT',
        path: '/eventos/1/contribuicoes/7',
        status: 422,
        body: { status: 422, message: 'Quantidade excede o necessário. Restam 1', path: '' },
      },
    ]);
    renderizar(evento);

    await userEvent.click(screen.getByRole('button', { name: 'Alterar' }));
    await userEvent.click(screen.getByRole('button', { name: 'Salvar' }));

    expect(await screen.findByText(/Restam 1/)).toBeInTheDocument();
  });

  it('esconde alterar e remover quando o evento está encerrado', () => {
    renderizar({ ...evento, status: 'ENCERRADO' });

    expect(screen.queryByRole('button', { name: 'Alterar' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Remover' })).not.toBeInTheDocument();
  });
});
