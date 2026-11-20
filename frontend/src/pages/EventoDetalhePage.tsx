import { useCallback } from 'react';
import { Link, useParams } from 'react-router-dom';
import { eventosApi, participacoesApi, participantesApi } from '../api';
import { ErrorAlert } from '../components/ErrorAlert';
import { Loading } from '../components/Loading';
import { StatusBadge } from '../components/StatusBadge';
import { TIPO_LABEL } from '../domain/evento';
import { formatarDataHora } from '../domain/formatadores';
import { useResource } from '../hooks/useResource';
import { ParticipacoesSection } from './evento/ParticipacoesSection';
import { StatusActions } from './evento/StatusActions';

/**
 * Tela central do desafio. A página carrega tudo do evento e passa para as seções;
 * quando uma seção grava algo, ela pede para recarregar só o que mudou.
 */
export function EventoDetalhePage() {
  const id = Number(useParams().id);

  const evento = useResource(useCallback(() => eventosApi.buscar(id), [id]));
  const participacoes = useResource(useCallback(() => participacoesApi.listar(id), [id]));
  const participantes = useResource(participantesApi.listar);

  if (evento.loading && !evento.data) return <Loading texto="Carregando evento..." />;
  if (!evento.data) {
    return (
      <div className="card">
        <ErrorAlert error={evento.error} />
        <Link to="/">Voltar para eventos</Link>
      </div>
    );
  }

  const ev = evento.data;

  return (
    <div className="stack">
      <Link to="/" className="back-link">
        ← Eventos
      </Link>

      <section className="card">
        <div className="card-header">
          <div>
            <h2>{ev.nome}</h2>
            <p className="muted">
              {TIPO_LABEL[ev.tipo]} · {formatarDataHora(ev.dataHora)}
            </p>
          </div>
          <StatusBadge status={ev.status} />
        </div>
        {ev.descricao && <p>{ev.descricao}</p>}
        <p className="small">
          Prazo para contribuições: <strong>{formatarDataHora(ev.prazoAlteracoes)}</strong>
        </p>
        <StatusActions evento={ev} onAlterado={evento.reload} />
      </section>

      <ParticipacoesSection evento={ev} participacoes={participacoes} participantes={participantes} />
    </div>
  );
}
