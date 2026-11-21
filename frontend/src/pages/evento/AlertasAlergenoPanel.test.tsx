import { render, screen } from '@testing-library/react';
import { describe, expect, it } from 'vitest';
import type { AlertaAlergeno } from '../../api/types';
import type { Resource } from '../../hooks/useResource';
import { AlertasAlergenoPanel } from './AlertasAlergenoPanel';

const recurso = (data: AlertaAlergeno[]): Resource<AlertaAlergeno[]> => ({ data, error: null, loading: false, reload: () => {} });

describe('AlertasAlergenoPanel', () => {
  it('mostra quem tem restrição a cada item', () => {
    render(
      <AlertasAlergenoPanel
        alertas={recurso([{ itemId: 3, itemNome: 'Bolo', participanteId: 1, participanteNome: 'Ana', alergenos: ['glúten'] }])}
      />,
    );

    expect(screen.getByRole('heading', { name: 'Atenção com alérgenos' })).toBeInTheDocument();
    expect(screen.getByRole('listitem')).toHaveTextContent('Bolo contém glúten, e Ana tem restrição.');
  });

  it('não mostra nada quando não há alerta', () => {
    const { container } = render(<AlertasAlergenoPanel alertas={recurso([])} />);
    expect(container).toBeEmptyDOMElement();
  });
});
