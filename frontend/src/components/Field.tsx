import type { ReactNode } from 'react';

interface Props {
  id: string;
  label: string;
  error?: string;
  hint?: string;
  children: ReactNode;
}

/** Label + campo + mensagem de erro do backend (ErrorResponse.fields[campo]). */
export function Field({ id, label, error, hint, children }: Props) {
  return (
    <div className={error ? 'field field-invalid' : 'field'}>
      <label htmlFor={id}>{label}</label>
      {children}
      {error ? (
        <small className="field-error" id={`${id}-erro`}>
          {error}
        </small>
      ) : (
        hint && <small className="field-hint">{hint}</small>
      )}
    </div>
  );
}
