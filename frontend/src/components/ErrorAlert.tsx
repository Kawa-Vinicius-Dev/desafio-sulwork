import type { ApiError } from '../api/http';

interface Props {
  error: ApiError | null;
}

/** Mensagem geral do erro. Os erros de cada campo aparecem no próprio Field. */
export function ErrorAlert({ error }: Props) {
  if (!error) return null;
  return (
    <div className="alert alert-error" role="alert">
      {error.message}
    </div>
  );
}
