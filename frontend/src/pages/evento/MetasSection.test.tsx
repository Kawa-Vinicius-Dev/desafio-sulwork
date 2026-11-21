import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import type { Evento, Item, MetaCategoria } from '../../api/types';
import type { Resource } from '../../hooks/useResource';
import { mockFetch } from '../../test/fetchMock';
import { MetasSection } from './MetasSection';

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

const cafe: Item = {
  id: 3,
  eventoId: 1,
  categoriaId: 5,
  categoriaNome: 'Bebidas',
  nome: 'Café',
  quantidadeNecessaria: 2,
  obrigatorio: true,
  alergenos: null,
};

const meta: MetaCategoria = { id: 9, eventoId: 1, categoriaId: 5, categoriaNome: 'Bebidas', percentual: 80 };

function renderizar(ev: Evento, metas: MetaCategoria[], onAlterado = vi.fn()) {
  render(<MetasSection evento={ev} itens={recurso([cafe])} metas={recurso(metas)} onAlterado={onAlterado} />);
  return onAlterado;
}

describe('MetasSection', () => {
  it('define a meta com PUT e recarrega a lista', async () => {
    const user = userEvent.setup();
    const fetch = mockFetch([{ method: 'PUT', path: '/eventos/1/metas/5', body: meta }]);
    const onAlterado = renderizar(evento, []);

    await user.selectOptions(screen.getByLabelText('Categoria'), 'Bebidas');
    await user.clear(screen.getByLabelText('Meta (%)'));
    await user.type(screen.getByLabelText('Meta (%)'), '80');
    await user.click(screen.getByRole('button', { name: 'Salvar meta' }));

    const [, init] = fetch.mock.calls[0];
    expect(init?.body).toBe(JSON.stringify({ percentual: 80 }));
    expect(onAlterado).toHaveBeenCalled();
  });

  it('remove a meta com DELETE depois de confirmar', async () => {
    const user = userEvent.setup();
    vi.spyOn(window, 'confirm').mockReturnValue(true);
    const fetch = mockFetch([{ method: 'DELETE', path: '/eventos/1/metas/5', status: 204 }]);
    const onAlterado = renderizar(evento, [meta]);

    expect(screen.getByText('80%')).toBeInTheDocument();
    await user.click(screen.getByRole('button', { name: 'Remover' }));

    expect(fetch).toHaveBeenCalledWith('/api/eventos/1/metas/5', expect.objectContaining({ method: 'DELETE' }));
    expect(onAlterado).toHaveBeenCalled();
  });

  it('esconde o formulário e o remover quando o evento está encerrado', () => {
    renderizar({ ...evento, status: 'ENCERRADO' }, [meta]);

    expect(screen.getByText('80%')).toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Salvar meta' })).not.toBeInTheDocument();
    expect(screen.queryByRole('button', { name: 'Remover' })).not.toBeInTheDocument();
  });
});
