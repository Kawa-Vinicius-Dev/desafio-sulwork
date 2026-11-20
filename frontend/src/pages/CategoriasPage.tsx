import { useState, type FormEvent } from 'react';
import { categoriasApi } from '../api';
import { ErrorAlert } from '../components/ErrorAlert';
import { Field } from '../components/Field';
import { Loading } from '../components/Loading';
import { useResource } from '../hooks/useResource';
import { useSubmit } from '../hooks/useSubmit';

export function CategoriasPage() {
  const categorias = useResource(categoriasApi.listar);
  const [nome, setNome] = useState('');
  const criar = useSubmit(categoriasApi.criar);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    if (await criar.run(nome.trim())) {
      setNome('');
      categorias.reload();
    }
  }

  return (
    <div className="page-grid">
      <section className="card">
        <h2>Categorias</h2>
        <p className="muted small">Usadas para agrupar os itens dos eventos (bebidas, salgados...).</p>
        {categorias.loading && !categorias.data && <Loading />}
        <ErrorAlert error={categorias.error} />
        {categorias.data?.length === 0 && <p className="muted">Nenhuma categoria cadastrada.</p>}
        {categorias.data && categorias.data.length > 0 && (
          <ul className="chips">
            {categorias.data.map((c) => (
              <li key={c.id} className="chip">
                {c.nome}
              </li>
            ))}
          </ul>
        )}
      </section>

      <section className="card">
        <h2>Nova categoria</h2>
        <form onSubmit={onSubmit}>
          <ErrorAlert error={criar.error} />
          <Field id="nome" label="Nome" error={criar.error?.fields.nome}>
            <input
              id="nome"
              value={nome}
              onChange={(e) => setNome(e.target.value)}
              maxLength={60}
              required
            />
          </Field>
          <button type="submit" className="btn btn-primary" disabled={criar.submitting}>
            {criar.submitting ? 'Salvando...' : 'Cadastrar categoria'}
          </button>
        </form>
      </section>
    </div>
  );
}
