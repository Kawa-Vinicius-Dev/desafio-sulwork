import { http } from './http';
import type {
  Categoria,
  Cobertura,
  Contribuicao,
  ContribuicaoRequest,
  Evento,
  EventoRequest,
  Faltante,
  Item,
  ItemRequest,
  MetaCategoria,
  Participacao,
  Participante,
  ParticipanteRequest,
  StatusEvento,
} from './types';

// Um objeto por recurso, cada função = um endpoint do backend.

export const eventosApi = {
  listar: () => http.get<Evento[]>('/eventos'),
  buscar: (id: number) => http.get<Evento>(`/eventos/${id}`),
  criar: (req: EventoRequest) => http.post<Evento>('/eventos', req),
  alterarStatus: (id: number, status: StatusEvento) =>
    http.patch<Evento>(`/eventos/${id}/status`, { status }),
};

export const participantesApi = {
  listar: () => http.get<Participante[]>('/participantes'),
  criar: (req: ParticipanteRequest) => http.post<Participante>('/participantes', req),
};

export const participacoesApi = {
  listar: (eventoId: number) => http.get<Participacao[]>(`/eventos/${eventoId}/participacoes`),
  inscrever: (eventoId: number, participanteId: number) =>
    http.post<Participacao>(`/eventos/${eventoId}/participacoes`, { participanteId }),
};

export const categoriasApi = {
  listar: () => http.get<Categoria[]>('/categorias'),
  criar: (nome: string) => http.post<Categoria>('/categorias', { nome }),
};

export const itensApi = {
  listar: (eventoId: number) => http.get<Item[]>(`/eventos/${eventoId}/itens`),
  adicionar: (eventoId: number, req: ItemRequest) =>
    http.post<Item>(`/eventos/${eventoId}/itens`, req),
};

export const contribuicoesApi = {
  listar: (eventoId: number) => http.get<Contribuicao[]>(`/eventos/${eventoId}/contribuicoes`),
  contribuir: (eventoId: number, itemId: number, req: ContribuicaoRequest) =>
    http.post<Contribuicao>(`/eventos/${eventoId}/itens/${itemId}/contribuicoes`, req),
  alterar: (eventoId: number, contribuicaoId: number, quantidade: number) =>
    http.put<Contribuicao>(`/eventos/${eventoId}/contribuicoes/${contribuicaoId}`, { quantidade }),
  remover: (eventoId: number, contribuicaoId: number) =>
    http.delete(`/eventos/${eventoId}/contribuicoes/${contribuicaoId}`),
  faltantes: (eventoId: number) => http.get<Faltante[]>(`/eventos/${eventoId}/faltantes`),
};

export const metasApi = {
  listar: (eventoId: number) => http.get<MetaCategoria[]>(`/eventos/${eventoId}/metas`),
  definir: (eventoId: number, categoriaId: number, percentual: number) =>
    http.put<MetaCategoria>(`/eventos/${eventoId}/metas/${categoriaId}`, { percentual }),
  remover: (eventoId: number, categoriaId: number) => http.delete(`/eventos/${eventoId}/metas/${categoriaId}`),
};

export const coberturaApi = {
  calcular: (eventoId: number) => http.get<Cobertura>(`/eventos/${eventoId}/cobertura`),
};

export { ApiError } from './http';
export type * from './types';
