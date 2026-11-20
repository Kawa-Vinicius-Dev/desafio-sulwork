import { useCallback, useState } from 'react';
import { ApiError, asApiError } from '../api/http';

/**
 * Envolve uma chamada de escrita (POST/PATCH): controla o "enviando..."
 * e guarda o erro da API para o formulário mostrar.
 * `run` devolve o resultado, ou undefined se deu erro.
 */
export function useSubmit<A extends unknown[], R>(action: (...args: A) => Promise<R>) {
  const [error, setError] = useState<ApiError | null>(null);
  const [submitting, setSubmitting] = useState(false);

  const run = useCallback(
    async (...args: A): Promise<R | undefined> => {
      setSubmitting(true);
      setError(null);
      try {
        return await action(...args);
      } catch (e) {
        setError(asApiError(e));
        return undefined;
      } finally {
        setSubmitting(false);
      }
    },
    [action],
  );

  return { run, error, submitting };
}
