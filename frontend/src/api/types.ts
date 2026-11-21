// Espelho dos DTOs do backend (pacote com.sulwork.dto).
// Datas chegam como LocalDateTime sem fuso: "2026-12-05T08:00:00".

export type TipoEvento = 'CAFE_DA_MANHA' | 'ALMOCO' | 'LANCHE' | 'CONFRATERNIZACAO' | 'OUTRO';

export type StatusEvento =
  | 'RASCUNHO'
  | 'ABERTO'
  | 'ENCERRADO'
  | 'REALIZADO'
  | 'CANCELADO';

export interface Evento {
  id: number;
  nome: string;
  descricao: string | null;
  tipo: TipoEvento;
  dataHora: string;
  prazoAlteracoes: string;
  status: StatusEvento;
}

export interface EventoRequest {
  nome: string;
  descricao?: string;
  tipo: TipoEvento;
  dataHora: string;
  prazoAlteracoes: string;
}

export interface Participante {
  id: number;
  nome: string;
  cpf: string;
  restricoesAlimentares: string | null;
}

export interface ParticipanteRequest {
  nome: string;
  cpf: string;
  restricoesAlimentares?: string;
}

export interface Participacao {
  id: number;
  eventoId: number;
  participanteId: number;
  participanteNome: string;
  inscritoEm: string;
}

export interface Categoria {
  id: number;
  nome: string;
}

export interface Item {
  id: number;
  eventoId: number;
  categoriaId: number;
  categoriaNome: string;
  nome: string;
  quantidadeNecessaria: number;
  obrigatorio: boolean;
  alergenos: string | null;
}

export interface ItemRequest {
  categoriaId: number;
  nome: string;
  quantidadeNecessaria: number;
  obrigatorio: boolean;
  alergenos?: string;
}

export interface Contribuicao {
  id: number;
  itemId: number;
  itemNome: string;
  participanteId: number;
  participanteNome: string;
  quantidade: number;
  criadoEm: string;
}

export interface ContribuicaoRequest {
  participanteId: number;
  quantidade: number;
}

export interface Faltante {
  itemId: number;
  nome: string;
  categoriaNome: string;
  quantidadeNecessaria: number;
  quantidadeConfirmada: number;
  faltam: number;
  obrigatorio: boolean;
}

export interface MetaCategoria {
  id: number;
  eventoId: number;
  categoriaId: number;
  categoriaNome: string;
  percentual: number;
}

/** Corpo padrão de erro do GlobalExceptionHandler. */
export interface ErrorResponse {
  timestamp: string;
  status: number;
  message: string;
  path: string;
  fields: Record<string, string> | null;
}
