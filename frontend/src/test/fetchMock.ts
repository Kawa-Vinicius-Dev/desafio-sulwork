import { vi } from 'vitest';

type Rota = { method?: string; path: string; status?: number; body?: unknown };

/** Substitui o fetch global por respostas fixas, casando método + caminho. */
export function mockFetch(rotas: Rota[]) {
  const fn = vi.fn(async (url: string, init?: RequestInit) => {
    const method = init?.method ?? 'GET';
    const rota = rotas.find((r) => (r.method ?? 'GET') === method && url === `/api${r.path}`);
    if (!rota) return new Response('', { status: 404 });
    const texto = rota.body === undefined ? '' : JSON.stringify(rota.body);
    // 204 No Content não pode ter corpo, nem string vazia
    return new Response(texto === '' ? null : texto, { status: rota.status ?? 200 });
  });
  vi.stubGlobal('fetch', fn);
  return fn;
}
