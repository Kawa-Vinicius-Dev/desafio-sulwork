import { useState, type FormEvent } from 'react';
import { metasApi } from '../../api';
import type { Evento, Item, MetaCategoria } from '../../api/types';
import { ErrorAlert } from '../../components/ErrorAlert';
import { Field } from '../../components/Field';
import { Loading } from '../../components/Loading';
import { aceitaItens } from '../../domain/evento';
import type { Resource } from '../../hooks/useResource';
import { useSubmit } from '../../hooks/useSubmit';

interface Props {
  evento: Evento;
  itens: Resource<Item[]>;
  metas: Resource<MetaCategoria[]>;
  onAlterado: () => void;
}

const FORM_VAZIO = { categoriaId: '', percentual: '100' };

/** O DELETE devolve 204 sem corpo; devolver true deixa claro para a tela que deu certo. */
async function removerMeta(eventoId: number, categoriaId: number) {
  await metasApi.remover(eventoId, categoriaId);
  return true;
}

/** Só faz sentido ter meta para categoria que tem item no evento. */
function categoriasDosItens(itens: Item[]) {
  const porId = new Map<number, string>();
  itens.forEach((i) => porId.set(i.categoriaId, i.categoriaNome));
  return [...porId].map(([id, nome]) => ({ id, nome })).sort((a, b) => a.nome.localeCompare(b.nome));
}

export function MetasSection({ evento, itens, metas, onAlterado }: Props) {
  const [form, setForm] = useState(FORM_VAZIO);
  const definir = useSubmit(metasApi.definir);
  const remover = useSubmit(removerMeta);
  const erro = definir.error?.fields ?? {};
  const podeAlterar = aceitaItens(evento);
  const categorias = categoriasDosItens(itens.data ?? []);

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    const salva = await definir.run(evento.id, Number(form.categoriaId), Number(form.percentual));
    if (salva) {
      setForm(FORM_VAZIO);
      onAlterado();
    }
  }

  async function onRemover(m: MetaCategoria) {
    if (!window.confirm(`Remover a meta de ${m.categoriaNome}?`)) return;
    if (await remover.run(evento.id, m.categoriaId)) onAlterado();
  }

  return (
    <section className="card">
      <h3>Metas por categoria</h3>
      <p className="muted small">Quanto de cada categoria precisa estar garantido para o evento acontecer.</p>
      {metas.loading && !metas.data && <Loading />}
      <ErrorAlert error={metas.error} />
      <ErrorAlert error={remover.error} />
      {metas.data?.length === 0 && <p className="muted">Nenhuma meta definida.</p>}
      {metas.data && metas.data.length > 0 && (
        <table className="table">
          <thead>
            <tr>
              <th>Categoria</th>
              <th className="num">Meta</th>
              {podeAlterar && <th>Ações</th>}
            </tr>
          </thead>
          <tbody>
            {metas.data.map((m) => (
              <tr key={m.id}>
                <td>{m.categoriaNome}</td>
                <td className="num">{m.percentual}%</td>
                {podeAlterar && (
                  <td>
                    <button
                      type="button"
                      className="btn btn-danger"
                      onClick={() => onRemover(m)}
                      disabled={remover.submitting}
                    >
                      Remover
                    </button>
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {!podeAlterar && <p className="muted small">Metas só podem ser alteradas em eventos em rascunho ou abertos.</p>}
      {podeAlterar && categorias.length === 0 && (
        <p className="muted small">Cadastre itens no evento para definir metas por categoria.</p>
      )}
      {podeAlterar && categorias.length > 0 && (
        <form onSubmit={onSubmit}>
          <h4>Definir meta</h4>
          <ErrorAlert error={definir.error} />
          <div className="row">
            <Field id="meta-categoria" label="Categoria" error={erro.categoriaId}>
              <select
                id="meta-categoria"
                value={form.categoriaId}
                onChange={(e) => setForm({ ...form, categoriaId: e.target.value })}
                required
              >
                <option value="">Selecione...</option>
                {categorias.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.nome}
                  </option>
                ))}
              </select>
            </Field>
            <Field id="meta-percentual" label="Meta (%)" error={erro.percentual}>
              <input
                id="meta-percentual"
                type="number"
                min={1}
                max={100}
                value={form.percentual}
                onChange={(e) => setForm({ ...form, percentual: e.target.value })}
                required
              />
            </Field>
          </div>
          <p className="muted small">Se a categoria já tem meta, o valor é substituído.</p>
          <button type="submit" className="btn btn-primary" disabled={definir.submitting}>
            Salvar meta
          </button>
        </form>
      )}
    </section>
  );
}
