import type { Cobertura } from '../../api/types';
import { ErrorAlert } from '../../components/ErrorAlert';
import { Loading } from '../../components/Loading';
import type { Resource } from '../../hooks/useResource';

interface Props {
  cobertura: Resource<Cobertura>;
}

/** Barra de progresso acessível: o leitor de tela lê o aria-label. */
function Barra({ rotulo, percentual }: { rotulo: string; percentual: number }) {
  return (
    <div
      className="progress"
      role="progressbar"
      aria-label={rotulo}
      aria-valuenow={percentual}
      aria-valuemin={0}
      aria-valuemax={100}
    >
      <div className="progress-bar" style={{ width: `${percentual}%` }} />
    </div>
  );
}

/** Mostra quanto do evento já está garantido. O cálculo é todo do backend; aqui só exibe. */
export function CoberturaPanel({ cobertura }: Props) {
  const c = cobertura.data;

  return (
    <section className="card">
      <h3>Cobertura do evento</h3>
      {cobertura.loading && !c && <Loading />}
      <ErrorAlert error={cobertura.error} />
      {c && (
        <>
          <p>
            <strong>{c.percentualGeral}%</strong> garantido{' '}
            <span className="muted small">(itens obrigatórios contam em dobro)</span>
          </p>
          <Barra rotulo={`Cobertura geral: ${c.percentualGeral}%`} percentual={c.percentualGeral} />

          {c.pronto ? (
            <p className="alert alert-ok">Tudo pronto: obrigatórios completos e metas atingidas.</p>
          ) : (
            <p className="alert alert-warn">
              {c.categorias.length === 0
                ? 'Cadastre itens para acompanhar a cobertura.'
                : !c.obrigatoriosCompletos
                  ? 'Ainda faltam itens obrigatórios.'
                  : 'Alguma categoria ainda não atingiu a meta.'}
            </p>
          )}

          {c.categorias.length > 0 && (
            <ul className="faltantes">
              {c.categorias.map((cat) => (
                <li key={cat.categoriaId}>
                  <div className="faltante-linha">
                    <span>{cat.categoriaNome}</span>
                    <strong>{cat.percentual}%</strong>
                  </div>
                  <Barra rotulo={`${cat.categoriaNome}: ${cat.percentual}%`} percentual={cat.percentual} />
                  <span className="muted small">
                    {cat.meta === null
                      ? 'sem meta'
                      : `meta ${cat.meta}% · ${cat.metaAtingida ? 'atingida' : 'não atingida'}`}
                  </span>
                </li>
              ))}
            </ul>
          )}
        </>
      )}
    </section>
  );
}
