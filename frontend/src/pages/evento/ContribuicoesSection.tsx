import { useState, type FormEvent } from 'react';
import { contribuicoesApi } from '../../api';
import type { Contribuicao, Evento, Item, Participacao } from '../../api/types';
import { ErrorAlert } from '../../components/ErrorAlert';
import { Field } from '../../components/Field';
import { Loading } from '../../components/Loading';
import { aceitaContribuicoes, prazoEncerrado } from '../../domain/evento';
import { formatarDataHora } from '../../domain/formatadores';
import type { Resource } from '../../hooks/useResource';
import { useSubmit } from '../../hooks/useSubmit';

interface Props {
  evento: Evento;
  itens: Resource<Item[]>;
  participacoes: Resource<Participacao[]>;
  contribuicoes: Resource<Contribuicao[]>;
  onContribuido: () => void;
}

const FORM_VAZIO = { participanteId: '', itemId: '', quantidade: '1' };

export function ContribuicoesSection({ evento, itens, participacoes, contribuicoes, onContribuido }: Props) {
  const [form, setForm] = useState(FORM_VAZIO);
  const contribuir = useSubmit(contribuicoesApi.contribuir);
  const erro = contribuir.error?.fields ?? {};

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    const criada = await contribuir.run(evento.id, Number(form.itemId), {
      participanteId: Number(form.participanteId),
      quantidade: Number(form.quantidade),
    });
    if (criada) {
      setForm(FORM_VAZIO);
      onContribuido();
    }
  }

  function motivoBloqueio(): string {
    if (evento.status !== 'ABERTO') return 'Contribuições só ficam disponíveis com o evento aberto.';
    if (prazoEncerrado(evento)) return `O prazo para contribuições terminou em ${formatarDataHora(evento.prazoAlteracoes)}.`;
    return '';
  }

  return (
    <section className="card">
      <h3>Contribuições</h3>
      {contribuicoes.loading && !contribuicoes.data && <Loading />}
      <ErrorAlert error={contribuicoes.error} />
      {contribuicoes.data?.length === 0 && <p className="muted">Nenhuma contribuição ainda.</p>}
      {contribuicoes.data && contribuicoes.data.length > 0 && (
        <table className="table">
          <thead>
            <tr>
              <th>Quem</th>
              <th>Item</th>
              <th className="num">Qtd.</th>
            </tr>
          </thead>
          <tbody>
            {contribuicoes.data.map((c) => (
              <tr key={c.id}>
                <td>{c.participanteNome}</td>
                <td>{c.itemNome}</td>
                <td className="num">{c.quantidade}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {aceitaContribuicoes(evento) ? (
        <form onSubmit={onSubmit}>
          <h4>Registrar contribuição</h4>
          <ErrorAlert error={contribuir.error} />
          <div className="row">
            <Field id="contrib-participante" label="Participante" error={erro.participanteId}>
              <select
                id="contrib-participante"
                value={form.participanteId}
                onChange={(e) => setForm({ ...form, participanteId: e.target.value })}
                required
              >
                <option value="">Selecione...</option>
                {participacoes.data?.map((p) => (
                  <option key={p.participanteId} value={p.participanteId}>
                    {p.participanteNome}
                  </option>
                ))}
              </select>
            </Field>
            <Field id="contrib-item" label="Item" error={erro.itemId}>
              <select
                id="contrib-item"
                value={form.itemId}
                onChange={(e) => setForm({ ...form, itemId: e.target.value })}
                required
              >
                <option value="">Selecione...</option>
                {itens.data?.map((i) => (
                  <option key={i.id} value={i.id}>
                    {i.nome}
                  </option>
                ))}
              </select>
            </Field>
            <Field id="contrib-qtd" label="Quantidade" error={erro.quantidade}>
              <input
                id="contrib-qtd"
                type="number"
                min={1}
                value={form.quantidade}
                onChange={(e) => setForm({ ...form, quantidade: e.target.value })}
                required
              />
            </Field>
          </div>
          <button type="submit" className="btn btn-primary" disabled={contribuir.submitting}>
            Registrar
          </button>
          <p className="muted small">Só participantes inscritos aparecem na lista.</p>
        </form>
      ) : (
        <p className="muted small">{motivoBloqueio()}</p>
      )}
    </section>
  );
}
