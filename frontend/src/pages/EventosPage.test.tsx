import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { describe, expect, it } from 'vitest';
import { mockFetch } from '../test/fetchMock';
import { EventosPage } from './EventosPage';

const renderPage = () =>
  render(
    <MemoryRouter>
      <EventosPage />
    </MemoryRouter>,
  );

describe('EventosPage', () => {
  it('lista os eventos com link para o detalhe', async () => {
    mockFetch([
      {
        path: '/eventos',
        body: [
          { id: 7, nome: 'Almoço de fim de ano', descricao: null, tipo: 'ALMOCO', dataHora: '2026-12-20T12:00:00', prazoAlteracoes: '2026-12-18T12:00:00', status: 'ABERTO' },
        ],
      },
    ]);

    renderPage();

    const link = await screen.findByRole('link', { name: 'Almoço de fim de ano' });
    expect(link).toHaveAttribute('href', '/eventos/7');
    expect(screen.getByText('Aberto', { selector: '.badge' })).toBeInTheDocument();
  });

  it('mostra a mensagem do backend e o erro de cada campo quando a criação falha', async () => {
    mockFetch([
      { path: '/eventos', body: [] },
      {
        method: 'POST',
        path: '/eventos',
        status: 400,
        body: { status: 400, message: 'Dados inválidos', fields: { dataHora: 'deve ser uma data futura' } },
      },
    ]);
    const user = userEvent.setup();
    renderPage();

    await user.type(screen.getByLabelText('Nome'), 'Café');
    await user.type(screen.getByLabelText('Data e hora'), '2020-01-01T08:00');
    await user.type(screen.getByLabelText('Prazo para contribuições'), '2019-12-31T08:00');
    await user.click(screen.getByRole('button', { name: 'Criar evento' }));

    expect(await screen.findByRole('alert')).toHaveTextContent('Dados inválidos');
    expect(screen.getByText('deve ser uma data futura')).toBeInTheDocument();
  });

  it('filtra a lista por status e pelo nome', async () => {
    mockFetch([
      {
        path: '/eventos',
        body: [
          { id: 1, nome: 'Café de sexta', descricao: null, tipo: 'CAFE_DA_MANHA', dataHora: '2026-12-04T08:00:00', prazoAlteracoes: '2026-12-03T18:00:00', status: 'ABERTO' },
          { id: 2, nome: 'Almoço de fim de ano', descricao: null, tipo: 'ALMOCO', dataHora: '2026-12-20T12:00:00', prazoAlteracoes: '2026-12-18T12:00:00', status: 'RASCUNHO' },
        ],
      },
    ]);
    const user = userEvent.setup();
    renderPage();
    await screen.findByRole('link', { name: 'Café de sexta' });

    await user.selectOptions(screen.getByLabelText('Filtrar por status'), 'RASCUNHO');
    expect(screen.queryByRole('link', { name: 'Café de sexta' })).not.toBeInTheDocument();
    expect(screen.getByRole('link', { name: 'Almoço de fim de ano' })).toBeInTheDocument();

    await user.type(screen.getByLabelText('Buscar evento pelo nome'), 'café');
    expect(screen.getByText('Nenhum evento com esse filtro.')).toBeInTheDocument();
  });
});
