import { useState, type FormEvent } from 'react';
import { Link } from 'react-router-dom';
import { eventosApi } from '../api';
import type { EventoRequest, StatusEvento, TipoEvento } from '../api/types';
import { ErrorAlert } from '../components/ErrorAlert';
import { Field } from '../components/Field';
import { Loading } from '../components/Loading';
import { StatusBadge } from '../components/StatusBadge';
import { STATUS_LABEL, TIPO_LABEL } from '../domain/evento';
import { formatarDataHora } from '../domain/formatadores';
import { useResource } from '../hooks/useResource';
import { useSubmit } from '../hooks/useSubmit';

const FORM_VAZIO: EventoRequest = {
  nome: '',
  descricao: '',
  tipo: 'CAFE_DA_MANHA',
  dataHora: '',
  prazoAlteracoes: '',
};

export function EventosPage() {
  const eventos = useResource(eventosApi.listar);
  const [form, setForm] = useState<EventoRequest>(FORM_VAZIO);
  const criar = useSubmit(eventosApi.criar);
  const [filtroStatus, setFiltroStatus] = useState<StatusEvento | ''>('');
  const [busca, setBusca] = useState('');

  const visiveis = (eventos.data ?? []).filter(
    (ev) =>
      (filtroStatus === '' || ev.status === filtroStatus) &&
      ev.nome.toLowerCase().includes(busca.trim().toLowerCase()),
  );

  const set = <K extends keyof EventoRequest>(campo: K, valor: EventoRequest[K]) =>
    setForm((f) => ({ ...f, [campo]: valor }));

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    const criado = await criar.run({ ...form, descricao: form.descricao?.trim() || undefined });
    if (criado) {
      setForm(FORM_VAZIO);
      eventos.reload();
    }
  }

  const erro = criar.error?.fields ?? {};

  return (
    <div className="page-grid">
      <section className="card">
        <h2>Eventos</h2>
        {eventos.loading && !eventos.data && <Loading />}
        <ErrorAlert error={eventos.error} />
        {eventos.data?.length === 0 && (
          <p className="muted">Nenhum evento ainda. Crie o primeiro ao lado.</p>
        )}
        {eventos.data && eventos.data.length > 0 && (
          <div className="filtros">
            <input
              aria-label="Buscar evento pelo nome"
              placeholder="Buscar pelo nome..."
              value={busca}
              onChange={(e) => setBusca(e.target.value)}
            />
            <select
              aria-label="Filtrar por status"
              value={filtroStatus}
              onChange={(e) => setFiltroStatus(e.target.value as StatusEvento | '')}
            >
              <option value="">Todos os status</option>
              {Object.entries(STATUS_LABEL).map(([valor, label]) => (
                <option key={valor} value={valor}>
                  {label}
                </option>
              ))}
            </select>
          </div>
        )}
        {eventos.data && eventos.data.length > 0 && visiveis.length === 0 && (
          <p className="muted">Nenhum evento com esse filtro.</p>
        )}
        {visiveis.length > 0 && (
          <ul className="list">
            {visiveis.map((ev) => (
              <li key={ev.id} className="list-item">
                <div>
                  <Link to={`/eventos/${ev.id}`} className="list-title">
                    {ev.nome}
                  </Link>
                  <p className="muted small">
                    {TIPO_LABEL[ev.tipo]} · {formatarDataHora(ev.dataHora)}
                  </p>
                </div>
                <StatusBadge status={ev.status} />
              </li>
            ))}
          </ul>
        )}
      </section>

      <section className="card">
        <h2>Novo evento</h2>
        <form onSubmit={onSubmit}>
          <ErrorAlert error={criar.error} />
          <Field id="nome" label="Nome" error={erro.nome}>
            <input
              id="nome"
              value={form.nome}
              onChange={(e) => set('nome', e.target.value)}
              maxLength={120}
              required
            />
          </Field>
          <Field id="tipo" label="Tipo" error={erro.tipo}>
            <select
              id="tipo"
              value={form.tipo}
              onChange={(e) => set('tipo', e.target.value as TipoEvento)}
            >
              {Object.entries(TIPO_LABEL).map(([valor, label]) => (
                <option key={valor} value={valor}>
                  {label}
                </option>
              ))}
            </select>
          </Field>
          <div className="row">
            <Field id="dataHora" label="Data e hora" error={erro.dataHora}>
              <input
                id="dataHora"
                type="datetime-local"
                value={form.dataHora}
                onChange={(e) => set('dataHora', e.target.value)}
                required
              />
            </Field>
            <Field
              id="prazoAlteracoes"
              label="Prazo para contribuições"
              error={erro.prazoAlteracoes}
              hint="Precisa ser antes do evento."
            >
              <input
                id="prazoAlteracoes"
                type="datetime-local"
                value={form.prazoAlteracoes}
                onChange={(e) => set('prazoAlteracoes', e.target.value)}
                required
              />
            </Field>
          </div>
          <Field id="descricao" label="Descrição (opcional)" error={erro.descricao}>
            <textarea
              id="descricao"
              rows={3}
              value={form.descricao}
              onChange={(e) => set('descricao', e.target.value)}
              maxLength={1000}
            />
          </Field>
          <button type="submit" className="btn btn-primary" disabled={criar.submitting}>
            {criar.submitting ? 'Criando...' : 'Criar evento'}
          </button>
        </form>
      </section>
    </div>
  );
}
