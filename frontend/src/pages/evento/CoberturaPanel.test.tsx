import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import type { Cobertura } from '../../api/types';
import type { Resource } from '../../hooks/useResource';
import { CoberturaPanel } from './CoberturaPanel';

const recurso = (data: Cobertura): Resource<Cobertura> => ({ data, error: null, loading: false, reload: () => {} });

describe('CoberturaPanel', () => {
  it('mostra o percentual geral e a meta de cada categoria', () => {
    render(
      <CoberturaPanel
        cobertura={recurso({
          percentualGeral: 60,
          obrigatoriosCompletos: true,
          pronto: false,
          categorias: [
            { categoriaId: 5, categoriaNome: 'Bebidas', percentual: 100, meta: null, metaAtingida: true },
            { categoriaId: 6, categoriaNome: 'Doces', percentual: 40, meta: 50, metaAtingida: false },
          ],
        })}
      />,
    );

    expect(screen.getByRole('progressbar', { name: 'Cobertura geral: 60%' })).toBeInTheDocument();
    expect(screen.getByText('meta 50% · não atingida')).toBeInTheDocument();
    expect(screen.getByText('sem meta')).toBeInTheDocument();
    expect(screen.getByText('Alguma categoria ainda não atingiu a meta.')).toBeInTheDocument();
  });

  it('avisa quando o evento está pronto', () => {
    render(
      <CoberturaPanel
        cobertura={recurso({
          percentualGeral: 100,
          obrigatoriosCompletos: true,
          pronto: true,
          categorias: [{ categoriaId: 5, categoriaNome: 'Bebidas', percentual: 100, meta: 100, metaAtingida: true }],
        })}
      />,
    );

    expect(screen.getByText('Tudo pronto: obrigatórios completos e metas atingidas.')).toBeInTheDocument();
  });
});
