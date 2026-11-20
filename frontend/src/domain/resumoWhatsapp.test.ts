import { describe, expect, it } from 'vitest';
import type { Evento, Faltante } from '../api/types';
import { montarResumoWhatsapp } from './resumoWhatsapp';

const evento: Evento = {
  id: 1,
  nome: 'Café de sexta',
  descricao: null,
  tipo: 'CAFE_DA_MANHA',
  dataHora: '2026-12-05T08:00:00',
  prazoAlteracoes: '2026-12-04T18:00:00',
  status: 'ABERTO',
};

const faltante = (parcial: Partial<Faltante>): Faltante => ({
  itemId: 1,
  nome: 'Item',
  categoriaNome: 'Geral',
  quantidadeNecessaria: 10,
  quantidadeConfirmada: 0,
  faltam: 10,
  obrigatorio: false,
  ...parcial,
});

describe('montarResumoWhatsapp', () => {
  it('lista o que falta, com obrigatórios primeiro e sem itens já completos', () => {
    const texto = montarResumoWhatsapp(evento, [
      faltante({ itemId: 1, nome: 'Suco', categoriaNome: 'Bebidas', quantidadeNecessaria: 4, faltam: 2 }),
      faltante({ itemId: 2, nome: 'Pão', categoriaNome: 'Padaria', quantidadeNecessaria: 20, faltam: 10, obrigatorio: true }),
      faltante({ itemId: 3, nome: 'Café', faltam: 0 }),
    ]);

    expect(texto).toContain('*Café de sexta* (Café da manhã)');
    expect(texto).toContain('Quando: 05/12/2026 às 08:00');
    expect(texto).toContain('Contribuições até: 04/12/2026 às 18:00');
    expect(texto).not.toContain('• Café');

    const linhas = texto.split('\n').filter((l) => l.startsWith('•'));
    expect(linhas).toEqual([
      '• Pão (Padaria): faltam 10 de 20 _(obrigatório)_',
      '• Suco (Bebidas): faltam 2 de 4',
    ]);
  });

  it('avisa quando está tudo confirmado', () => {
    const texto = montarResumoWhatsapp(evento, [faltante({ faltam: 0 })]);
    expect(texto).toContain('Tudo confirmado!');
    expect(texto).not.toContain('O que ainda falta');
  });
});
