import { describe, expect, it, vi } from 'vitest';
import { ApiError, http } from './http';
import { mockFetch } from '../test/fetchMock';

async function capturarErro(chamada: () => Promise<unknown>): Promise<ApiError> {
  try {
    await chamada();
  } catch (e) {
    return e as ApiError;
  }
  throw new Error('a chamada deveria ter falhado');
}

describe('http', () => {
  it('envia JSON e devolve o corpo da resposta', async () => {
    const fetch = mockFetch([{ method: 'POST', path: '/categorias', status: 201, body: { id: 1, nome: 'Bebidas' } }]);

    const criada = await http.post('/categorias', { nome: 'Bebidas' });

    expect(criada).toEqual({ id: 1, nome: 'Bebidas' });
    const [, init] = fetch.mock.calls[0];
    expect(init?.headers).toEqual({ 'Content-Type': 'application/json' });
    expect(init?.body).toBe('{"nome":"Bebidas"}');
  });

  it('transforma o ErrorResponse em ApiError com os erros de cada campo', async () => {
    mockFetch([
      {
        method: 'POST',
        path: '/participantes',
        status: 400,
        body: { status: 400, message: 'Dados inválidos', path: '/api/participantes', fields: { cpf: 'CPF inválido' } },
      },
    ]);

    const erro = await capturarErro(() => http.post('/participantes', {}));

    expect(erro).toBeInstanceOf(ApiError);
    expect(erro.status).toBe(400);
    expect(erro.message).toBe('Dados inválidos');
    expect(erro.fields).toEqual({ cpf: 'CPF inválido' });
  });

  it('usa mensagem padrão quando o corpo de erro não segue o ErrorResponse', async () => {
    mockFetch([]); // qualquer rota devolve 404 sem corpo

    const erro = await capturarErro(() => http.get('/eventos/99'));

    expect(erro).toMatchObject({ status: 404, message: 'Recurso não encontrado.', fields: {} });
  });

  it('avisa quando o backend está fora do ar', async () => {
    vi.stubGlobal('fetch', vi.fn().mockRejectedValue(new TypeError('Failed to fetch')));

    const erro = await capturarErro(() => http.get('/eventos'));

    expect(erro).toMatchObject({ status: 0 });
    expect(erro.message).toMatch(/backend está rodando/);
  });
});
