import type { Evento, StatusEvento, TipoEvento } from '../api/types';

export const TIPO_LABEL: Record<TipoEvento, string> = {
  CAFE_DA_MANHA: 'Café da manhã',
  ALMOCO: 'Almoço',
  LANCHE: 'Lanche',
  CONFRATERNIZACAO: 'Confraternização',
  OUTRO: 'Outro',
};

export const STATUS_LABEL: Record<StatusEvento, string> = {
  RASCUNHO: 'Rascunho',
  ABERTO: 'Aberto',
  INSCRICOES_ENCERRADAS: 'Inscrições encerradas',
  REALIZADO: 'Realizado',
  CANCELADO: 'Cancelado',
};

/**
 * Mesmas transições de StatusEvento.podeMudarPara no backend.
 * O front usa só para decidir quais botões mostrar; quem valida de verdade é a API.
 */
export const PROXIMOS_STATUS: Record<StatusEvento, StatusEvento[]> = {
  RASCUNHO: ['ABERTO', 'CANCELADO'],
  ABERTO: ['INSCRICOES_ENCERRADAS', 'CANCELADO'],
  INSCRICOES_ENCERRADAS: ['REALIZADO', 'CANCELADO'],
  REALIZADO: [],
  CANCELADO: [],
};

/** Texto do botão que leva o evento para cada status. */
export const ACAO_STATUS: Record<StatusEvento, string> = {
  RASCUNHO: 'Voltar para rascunho',
  ABERTO: 'Abrir inscrições',
  INSCRICOES_ENCERRADAS: 'Encerrar inscrições',
  REALIZADO: 'Marcar como realizado',
  CANCELADO: 'Cancelar evento',
};

export function aceitaInscricoes(evento: Evento): boolean {
  return evento.status === 'ABERTO';
}

export function aceitaItens(evento: Evento): boolean {
  return evento.status === 'RASCUNHO' || evento.status === 'ABERTO';
}

export function prazoEncerrado(evento: Evento, agora: Date = new Date()): boolean {
  return agora >= new Date(evento.prazoAlteracoes);
}

export function aceitaContribuicoes(evento: Evento, agora: Date = new Date()): boolean {
  return evento.status === 'ABERTO' && !prazoEncerrado(evento, agora);
}
