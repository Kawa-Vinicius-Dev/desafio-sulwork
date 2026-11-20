const BASE_URL = '/api';

/**
 * Erro vindo da API (ou da falta dela). Sempre tem uma mensagem pronta para
 * mostrar ao usuário e, nos 400 de validação, a mensagem de cada campo.
 */
export class ApiError extends Error {
  constructor(
    readonly status: number,
    message: string,
    readonly fields: Record<string, string> = {},
  ) {
    super(message);
    this.name = 'ApiError';
  }
}

const MENSAGEM_PADRAO: Record<number, string> = {
  400: 'Dados inválidos.',
  404: 'Recurso não encontrado.',
  409: 'Conflito com dados existentes.',
  422: 'A operação viola uma regra do evento.',
  500: 'Erro inesperado no servidor.',
};

function parseJson(text: string): unknown {
  try {
    return JSON.parse(text);
  } catch {
    return undefined;
  }
}

/** Converte o corpo de uma resposta de erro em ApiError, mesmo que não siga o ErrorResponse. */
export function toApiError(status: number, body: unknown): ApiError {
  const obj = typeof body === 'object' && body !== null ? (body as Record<string, unknown>) : {};
  const message =
    typeof obj.message === 'string' && obj.message.trim() !== ''
      ? obj.message
      : (MENSAGEM_PADRAO[status] ?? `Erro ${status}.`);
  const fields =
    typeof obj.fields === 'object' && obj.fields !== null
      ? (obj.fields as Record<string, string>)
      : {};
  return new ApiError(status, message, fields);
}

/** Garante que qualquer coisa capturada num catch vire ApiError. */
export function asApiError(e: unknown): ApiError {
  if (e instanceof ApiError) return e;
  return new ApiError(0, e instanceof Error ? e.message : 'Erro desconhecido.');
}

async function request<T>(method: string, path: string, body?: unknown): Promise<T> {
  let response: Response;
  try {
    response = await fetch(BASE_URL + path, {
      method,
      headers: body !== undefined ? { 'Content-Type': 'application/json' } : undefined,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    });
  } catch {
    throw new ApiError(0, 'Não foi possível conectar ao servidor. Verifique se o backend está rodando.');
  }

  const text = await response.text();
  const data = text ? parseJson(text) : undefined;

  if (!response.ok) {
    throw toApiError(response.status, data);
  }
  return data as T;
}

export const http = {
  get: <T>(path: string) => request<T>('GET', path),
  post: <T>(path: string, body: unknown) => request<T>('POST', path, body),
  patch: <T>(path: string, body: unknown) => request<T>('PATCH', path, body),
};
