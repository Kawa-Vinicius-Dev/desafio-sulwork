import { useState, type FormEvent } from 'react';
import { Link } from 'react-router-dom';
import { participacoesApi } from '../../api';
import type { Evento, Participacao, Participante } from '../../api/types';
import { ErrorAlert } from '../../components/ErrorAlert';
import { Field } from '../../components/Field';
import { Loading } from '../../components/Loading';
import { aceitaInscricoes } from '../../domain/evento';
import { formatarDataHora } from '../../domain/formatadores';
import type { Resource } from '../../hooks/useResource';
import { useSubmit } from '../../hooks/useSubmit';

interface Props {
  evento: Evento;
  participacoes: Resource<Participacao[]>;
  participantes: Resource<Participante[]>;
}

export function ParticipacoesSection({ evento, participacoes, participantes }: Props) {
  const [participanteId, setParticipanteId] = useState('');
  const inscrever = useSubmit(participacoesApi.inscrever);

  const inscritos = new Set(participacoes.data?.map((p) => p.participanteId));
  const disponiveis = (participantes.data ?? []).filter((p) => !inscritos.has(p.id));

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    if (await inscrever.run(evento.id, Number(participanteId))) {
      setParticipanteId('');
      participacoes.reload();
    }
  }

  return (
    <section className="card">
      <h3>Participantes inscritos</h3>
      {participacoes.loading && !participacoes.data && <Loading />}
      <ErrorAlert error={participacoes.error} />
      {participacoes.data?.length === 0 && <p className="muted">Ninguém inscrito ainda.</p>}
      {participacoes.data && participacoes.data.length > 0 && (
        <ul className="list compact">
          {participacoes.data.map((p) => (
            <li key={p.id} className="list-item">
              <span>{p.participanteNome}</span>
              <span className="muted small">inscrito em {formatarDataHora(p.inscritoEm)}</span>
            </li>
          ))}
        </ul>
      )}

      {aceitaInscricoes(evento) ? (
        <form onSubmit={onSubmit} className="inline-form">
          <ErrorAlert error={inscrever.error} />
          <Field id="participanteId" label="Inscrever participante" error={inscrever.error?.fields.participanteId}>
            <select
              id="participanteId"
              value={participanteId}
              onChange={(e) => setParticipanteId(e.target.value)}
              required
            >
              <option value="">Selecione...</option>
              {disponiveis.map((p) => (
                <option key={p.id} value={p.id}>
                  {p.nome}
                </option>
              ))}
            </select>
          </Field>
          <button type="submit" className="btn btn-primary" disabled={inscrever.submitting}>
            Inscrever
          </button>
          {participantes.data?.length === 0 && (
            <p className="muted small">
              Nenhum participante cadastrado. <Link to="/participantes">Cadastrar agora</Link>.
            </p>
          )}
        </form>
      ) : (
        <p className="muted small">Inscrições só ficam disponíveis com o evento aberto.</p>
      )}
    </section>
  );
}
