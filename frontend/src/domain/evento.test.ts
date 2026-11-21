import { describe, expect, it } from 'vitest';
import type { Evento } from '../api/types';
import { PROXIMOS_STATUS, aceitaContribuicoes, aceitaItens } from './evento';

const evento = (parcial: Partial<Evento>): Evento => ({
  id: 1,
  nome: 'Café',
  descricao: null,
  tipo: 'CAFE_DA_MANHA',
  dataHora: '2026-12-05T08:00:00',
  prazoAlteracoes: '2026-12-04T18:00:00',
  status: 'ABERTO',
  ...parcial,
});

describe('regras de evento no front', () => {
  it('segue as mesmas transições do backend e não volta status', () => {
    expect(PROXIMOS_STATUS.RASCUNHO).toEqual(['ABERTO', 'CANCELADO']);
    expect(PROXIMOS_STATUS.ABERTO).toEqual(['ENCERRADO', 'CANCELADO']);
    expect(PROXIMOS_STATUS.ENCERRADO).toEqual(['REALIZADO', 'CANCELADO']);
    expect(PROXIMOS_STATUS.REALIZADO).toEqual([]);
    expect(PROXIMOS_STATUS.CANCELADO).toEqual([]);
  });

  it('aceita contribuições só com evento aberto e antes do prazo', () => {
    const antes = new Date('2026-12-04T17:59:00');
    const depois = new Date('2026-12-04T18:00:00');

    expect(aceitaContribuicoes(evento({}), antes)).toBe(true);
    expect(aceitaContribuicoes(evento({}), depois)).toBe(false);
    expect(aceitaContribuicoes(evento({ status: 'RASCUNHO' }), antes)).toBe(false);
  });

  it('aceita itens em rascunho e aberto', () => {
    expect(aceitaItens(evento({ status: 'RASCUNHO' }))).toBe(true);
    expect(aceitaItens(evento({ status: 'ABERTO' }))).toBe(true);
    expect(aceitaItens(evento({ status: 'ENCERRADO' }))).toBe(false);
  });
});
