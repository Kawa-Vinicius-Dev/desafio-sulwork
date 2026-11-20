import { useState, type FormEvent } from 'react';
import { participantesApi } from '../api';
import type { ParticipanteRequest } from '../api/types';
import { ErrorAlert } from '../components/ErrorAlert';
import { Field } from '../components/Field';
import { Loading } from '../components/Loading';
import { formatarCpf } from '../domain/formatadores';
import { useResource } from '../hooks/useResource';
import { useSubmit } from '../hooks/useSubmit';

const FORM_VAZIO: ParticipanteRequest = { nome: '', cpf: '', restricoesAlimentares: '' };

export function ParticipantesPage() {
  const participantes = useResource(participantesApi.listar);
  const [form, setForm] = useState<ParticipanteRequest>(FORM_VAZIO);
  const criar = useSubmit(participantesApi.criar);
  const erro = criar.error?.fields ?? {};

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    const criado = await criar.run({
      nome: form.nome.trim(),
      cpf: form.cpf,
      restricoesAlimentares: form.restricoesAlimentares?.trim() || undefined,
    });
    if (criado) {
      setForm(FORM_VAZIO);
      participantes.reload();
    }
  }

  return (
    <div className="page-grid">
      <section className="card">
        <h2>Participantes</h2>
        {participantes.loading && !participantes.data && <Loading />}
        <ErrorAlert error={participantes.error} />
        {participantes.data?.length === 0 && <p className="muted">Nenhum participante cadastrado.</p>}
        {participantes.data && participantes.data.length > 0 && (
          <table className="table">
            <thead>
              <tr>
                <th>Nome</th>
                <th>CPF</th>
                <th>Restrições alimentares</th>
              </tr>
            </thead>
            <tbody>
              {participantes.data.map((p) => (
                <tr key={p.id}>
                  <td>{p.nome}</td>
                  <td className="nowrap">{formatarCpf(p.cpf)}</td>
                  <td>{p.restricoesAlimentares || <span className="muted">Nenhuma</span>}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>

      <section className="card">
        <h2>Novo participante</h2>
        <form onSubmit={onSubmit}>
          <ErrorAlert error={criar.error} />
          <Field id="nome" label="Nome" error={erro.nome}>
            <input
              id="nome"
              value={form.nome}
              onChange={(e) => setForm({ ...form, nome: e.target.value })}
              maxLength={120}
              required
            />
          </Field>
          <Field id="cpf" label="CPF" error={erro.cpf} hint="Com ou sem pontuação.">
            <input
              id="cpf"
              inputMode="numeric"
              value={form.cpf}
              onChange={(e) => setForm({ ...form, cpf: e.target.value })}
              maxLength={14}
              required
            />
          </Field>
          <Field
            id="restricoes"
            label="Restrições alimentares (opcional)"
            error={erro.restricoesAlimentares}
          >
            <textarea
              id="restricoes"
              rows={2}
              placeholder="Ex.: sem lactose, vegetariano"
              value={form.restricoesAlimentares}
              onChange={(e) => setForm({ ...form, restricoesAlimentares: e.target.value })}
              maxLength={1000}
            />
          </Field>
          <button type="submit" className="btn btn-primary" disabled={criar.submitting}>
            {criar.submitting ? 'Salvando...' : 'Cadastrar participante'}
          </button>
        </form>
      </section>
    </div>
  );
}
