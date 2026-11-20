import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { describe, expect, it, vi } from 'vitest';
import type { Evento, Faltante } from '../../api/types';
import type { Resource } from '../../hooks/useResource';
import { FaltantesPanel } from './FaltantesPanel';

const evento: Evento = {
  id: 1,
  nome: 'Café de sexta',
  descricao: null,
  tipo: 'CAFE_DA_MANHA',
  dataHora: '2026-12-05T08:00:00',
  prazoAlteracoes: '2026-12-04T18:00:00',
  status: 'ABERTO',
};

const recurso = (data: Faltante[]): Resource<Faltante[]> => ({ data, error: null, loading: false, reload: () => {} });

describe('FaltantesPanel', () => {
  it('desabilita o botão enquanto não há itens', () => {
    render(<FaltantesPanel evento={evento} faltantes={recurso([])} />);
    expect(screen.getByRole('button', { name: 'Copiar resumo para WhatsApp' })).toBeDisabled();
  });

  it('mostra o progresso e copia o resumo para a área de transferência', async () => {
    const user = userEvent.setup();
    const writeText = vi.spyOn(navigator.clipboard, 'writeText').mockResolvedValue();

    render(
      <FaltantesPanel
        evento={evento}
        faltantes={recurso([
          { itemId: 1, nome: 'Pão', categoriaNome: 'Padaria', quantidadeNecessaria: 20, quantidadeConfirmada: 15, faltam: 5, obrigatorio: true },
        ])}
      />,
    );

    expect(screen.getByText('faltam 5')).toBeInTheDocument();
    expect(screen.getByRole('progressbar', { name: 'Pão: 15 de 20' })).toBeInTheDocument();

    await user.click(screen.getByRole('button', { name: 'Copiar resumo para WhatsApp' }));

    expect(writeText).toHaveBeenCalledWith(expect.stringContaining('• Pão (Padaria): faltam 5 de 20'));
    expect(screen.getByRole('button', { name: 'Copiado!' })).toBeInTheDocument();
  });

  it('mostra o texto para copiar à mão se a área de transferência falhar', async () => {
    const user = userEvent.setup();
    vi.spyOn(navigator.clipboard, 'writeText').mockRejectedValue(new Error('negado'));

    render(
      <FaltantesPanel
        evento={evento}
        faltantes={recurso([
          { itemId: 1, nome: 'Café', categoriaNome: 'Bebidas', quantidadeNecessaria: 2, quantidadeConfirmada: 2, faltam: 0, obrigatorio: false },
        ])}
      />,
    );
    await user.click(screen.getByRole('button', { name: 'Copiar resumo para WhatsApp' }));

    const texto = screen.getByLabelText<HTMLTextAreaElement>('Resumo para WhatsApp').value;
    expect(texto).toContain('Tudo confirmado!');
  });
});
