import { useState, type FormEvent } from 'react';
import { Link } from 'react-router-dom';
import { itensApi } from '../../api';
import type { Categoria, Evento, Item } from '../../api/types';
import { ErrorAlert } from '../../components/ErrorAlert';
import { Field } from '../../components/Field';
import { Loading } from '../../components/Loading';
import { aceitaItens } from '../../domain/evento';
import type { Resource } from '../../hooks/useResource';
import { useSubmit } from '../../hooks/useSubmit';

interface Props {
  evento: Evento;
  itens: Resource<Item[]>;
  categorias: Resource<Categoria[]>;
  onAdicionado: () => void;
}

const FORM_VAZIO = { categoriaId: '', nome: '', quantidadeNecessaria: '1', obrigatorio: false, alergenos: '' };

export function ItensSection({ evento, itens, categorias, onAdicionado }: Props) {
  const [form, setForm] = useState(FORM_VAZIO);
  const adicionar = useSubmit(itensApi.adicionar);
  const erro = adicionar.error?.fields ?? {};

  async function onSubmit(e: FormEvent) {
    e.preventDefault();
    const criado = await adicionar.run(evento.id, {
      categoriaId: Number(form.categoriaId),
      nome: form.nome.trim(),
      quantidadeNecessaria: Number(form.quantidadeNecessaria),
      obrigatorio: form.obrigatorio,
      alergenos: form.alergenos.trim() || undefined,
    });
    if (criado) {
      setForm(FORM_VAZIO);
      onAdicionado();
    }
  }

  return (
    <section className="card">
      <h3>Itens necessários</h3>
      {itens.loading && !itens.data && <Loading />}
      <ErrorAlert error={itens.error} />
      {itens.data?.length === 0 && <p className="muted">Nenhum item cadastrado.</p>}
      {itens.data && itens.data.length > 0 && (
        <table className="table">
          <thead>
            <tr>
              <th>Item</th>
              <th>Categoria</th>
              <th className="num">Qtd.</th>
              <th>Alérgenos</th>
            </tr>
          </thead>
          <tbody>
            {itens.data.map((i) => (
              <tr key={i.id}>
                <td>
                  {i.nome} {i.obrigatorio && <span className="tag">obrigatório</span>}
                </td>
                <td>{i.categoriaNome}</td>
                <td className="num">{i.quantidadeNecessaria}</td>
                <td>{i.alergenos || <span className="muted">-</span>}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      {aceitaItens(evento) ? (
        <form onSubmit={onSubmit}>
          <h4>Adicionar item</h4>
          <ErrorAlert error={adicionar.error} />
          <div className="row">
            <Field id="item-nome" label="Nome" error={erro.nome}>
              <input
                id="item-nome"
                value={form.nome}
                onChange={(e) => setForm({ ...form, nome: e.target.value })}
                maxLength={120}
                required
              />
            </Field>
            <Field id="item-categoria" label="Categoria" error={erro.categoriaId}>
              <select
                id="item-categoria"
                value={form.categoriaId}
                onChange={(e) => setForm({ ...form, categoriaId: e.target.value })}
                required
              >
                <option value="">Selecione...</option>
                {categorias.data?.map((c) => (
                  <option key={c.id} value={c.id}>
                    {c.nome}
                  </option>
                ))}
              </select>
            </Field>
            <Field id="item-qtd" label="Quantidade" error={erro.quantidadeNecessaria}>
              <input
                id="item-qtd"
                type="number"
                min={1}
                value={form.quantidadeNecessaria}
                onChange={(e) => setForm({ ...form, quantidadeNecessaria: e.target.value })}
                required
              />
            </Field>
          </div>
          <Field id="item-alergenos" label="Alérgenos (opcional)" error={erro.alergenos}>
            <input
              id="item-alergenos"
              placeholder="Ex.: glúten, lactose"
              value={form.alergenos}
              onChange={(e) => setForm({ ...form, alergenos: e.target.value })}
              maxLength={500}
            />
          </Field>
          <label className="checkbox">
            <input
              type="checkbox"
              checked={form.obrigatorio}
              onChange={(e) => setForm({ ...form, obrigatorio: e.target.checked })}
            />
            Item obrigatório
          </label>
          <button type="submit" className="btn btn-primary" disabled={adicionar.submitting}>
            Adicionar item
          </button>
          {categorias.data?.length === 0 && (
            <p className="muted small">
              Nenhuma categoria cadastrada. <Link to="/categorias">Cadastrar agora</Link>.
            </p>
          )}
        </form>
      ) : (
        <p className="muted small">Itens só podem ser adicionados em eventos em rascunho ou abertos.</p>
      )}
    </section>
  );
}
