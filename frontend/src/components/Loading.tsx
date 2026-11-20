export function Loading({ texto = 'Carregando...' }: { texto?: string }) {
  return (
    <p className="muted" aria-live="polite">
      {texto}
    </p>
  );
}
