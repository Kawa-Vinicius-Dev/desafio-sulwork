import { useCallback, useEffect, useState } from 'react';
import { ApiError, asApiError } from '../api/http';

interface ResourceState<T> {
  data: T | undefined;
  error: ApiError | null;
  loading: boolean;
}

export type Resource<T> = ResourceState<T> & { reload: () => void };

/**
 * Carrega um dado da API ao montar o componente e sempre que `loader` mudar.
 * `loader` deve ser estável (useCallback), senão recarrega a cada render.
 * `reload()` busca de novo, por exemplo depois de um cadastro.
 */
export function useResource<T>(loader: () => Promise<T>): Resource<T> {
  const [state, setState] = useState<ResourceState<T>>({
    data: undefined,
    error: null,
    loading: true,
  });
  const [versao, setVersao] = useState(0);

  useEffect(() => {
    let ativo = true; // evita atualizar estado de um componente que já saiu da tela
    setState((s) => ({ ...s, loading: true }));
    loader().then(
      (data) => ativo && setState({ data, error: null, loading: false }),
      (e) => ativo && setState((s) => ({ ...s, error: asApiError(e), loading: false })),
    );
    return () => {
      ativo = false;
    };
  }, [loader, versao]);

  const reload = useCallback(() => setVersao((v) => v + 1), []);

  return { ...state, reload };
}
