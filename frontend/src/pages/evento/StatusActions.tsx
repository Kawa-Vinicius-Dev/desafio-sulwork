import { eventosApi } from '../../api';
import type { Evento, StatusEvento } from '../../api/types';
import { ErrorAlert } from '../../components/ErrorAlert';
import { ACAO_STATUS, PROXIMOS_STATUS } from '../../domain/evento';
import { useSubmit } from '../../hooks/useSubmit';

interface Props {
  evento: Evento;
  onAlterado: () => void;
}

/** Botões só para as transições permitidas a partir do status atual. */
export function StatusActions({ evento, onAlterado }: Props) {
  const alterar = useSubmit(eventosApi.alterarStatus);
  const proximos = PROXIMOS_STATUS[evento.status];

  async function mudarPara(status: StatusEvento) {
    if (status === 'CANCELADO' && !window.confirm('Cancelar o evento? Isso não pode ser desfeito.')) {
      return;
    }
    if (await alterar.run(evento.id, status)) onAlterado();
  }

  if (proximos.length === 0) {
    return <p className="muted small">Este evento está finalizado e não muda mais de status.</p>;
  }

  return (
    <div className="status-actions">
      <div className="btn-group">
        {proximos.map((status) => (
          <button
            key={status}
            type="button"
            className={status === 'CANCELADO' ? 'btn btn-danger' : 'btn btn-primary'}
            disabled={alterar.submitting}
            onClick={() => mudarPara(status)}
          >
            {ACAO_STATUS[status]}
          </button>
        ))}
      </div>
      <ErrorAlert error={alterar.error} />
    </div>
  );
}
