import { useCallback } from 'react';
import { Link, useParams } from 'react-router-dom';
import {
  categoriasApi,
  contribuicoesApi,
  eventosApi,
  itensApi,
  metasApi,
  participacoesApi,
  participantesApi,
} from '../api';
import { ErrorAlert } from '../components/ErrorAlert';
import { Loading } from '../components/Loading';
import { StatusBadge } from '../components/StatusBadge';
import { TIPO_LABEL } from '../domain/evento';
import { formatarDataHora } from '../domain/formatadores';
import { useResource } from '../hooks/useResource';
import { ContribuicoesSection } from './evento/ContribuicoesSection';
import { FaltantesPanel } from './evento/FaltantesPanel';
import { ItensSection } from './evento/ItensSection';
import { MetasSection } from './evento/MetasSection';
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
  const itens = useResource(useCallback(() => itensApi.listar(id), [id]));
  const contribuicoes = useResource(useCallback(() => contribuicoesApi.listar(id), [id]));
  const faltantes = useResource(useCallback(() => contribuicoesApi.faltantes(id), [id]));
  const metas = useResource(useCallback(() => metasApi.listar(id), [id]));
  const participantes = useResource(participantesApi.listar);
  const categorias = useResource(categoriasApi.listar);

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

  const aoAdicionarItem = () => {
    itens.reload();
    faltantes.reload();
  };
  const aoContribuir = () => {
    contribuicoes.reload();
    faltantes.reload();
  };

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

      <div className="page-grid">
        <div className="stack">
          <ItensSection evento={ev} itens={itens} categorias={categorias} onAdicionado={aoAdicionarItem} />
          <ContribuicoesSection
            evento={ev}
            itens={itens}
            participacoes={participacoes}
            contribuicoes={contribuicoes}
            onContribuido={aoContribuir}
          />
        </div>
        <div className="stack">
          <FaltantesPanel evento={ev} faltantes={faltantes} />
          <MetasSection evento={ev} itens={itens} metas={metas} onAlterado={metas.reload} />
          <ParticipacoesSection evento={ev} participacoes={participacoes} participantes={participantes} />
        </div>
      </div>
    </div>
  );
}
