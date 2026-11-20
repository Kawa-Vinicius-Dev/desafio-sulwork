import { useEffect, useState } from 'react';
import type { Evento, Faltante } from '../../api/types';
import { ErrorAlert } from '../../components/ErrorAlert';
import { Loading } from '../../components/Loading';
import { montarResumoWhatsapp } from '../../domain/resumoWhatsapp';
import type { Resource } from '../../hooks/useResource';

interface Props {
  evento: Evento;
  faltantes: Resource<Faltante[]>;
}

type EstadoCopia = 'ocioso' | 'copiado' | 'falhou';

/**
 * Funcionalidade livre: mostra o que ainda falta e copia um resumo
 * pronto para colar no grupo do WhatsApp.
 */
export function FaltantesPanel({ evento, faltantes }: Props) {
  const [copia, setCopia] = useState<EstadoCopia>('ocioso');
  const lista = faltantes.data ?? [];
  const pendentes = lista.filter((f) => f.faltam > 0);
  const resumo = montarResumoWhatsapp(evento, lista);

  // Volta o botão ao texto normal depois de alguns segundos.
  useEffect(() => {
    if (copia !== 'copiado') return;
    const timer = setTimeout(() => setCopia('ocioso'), 2500);
    return () => clearTimeout(timer);
  }, [copia]);

  async function copiar() {
    try {
      await navigator.clipboard.writeText(resumo);
      setCopia('copiado');
    } catch {
      // Sem permissão de área de transferência (ex.: http fora do localhost): mostra o texto para copiar à mão.
      setCopia('falhou');
    }
  }

  return (
    <section className="card card-highlight">
      <div className="card-header">
        <h3>O que falta</h3>
        <button
          type="button"
          className="btn btn-whatsapp"
          onClick={copiar}
          disabled={lista.length === 0}
        >
          {copia === 'copiado' ? 'Copiado!' : 'Copiar resumo para WhatsApp'}
        </button>
      </div>

      {faltantes.loading && !faltantes.data && <Loading />}
      <ErrorAlert error={faltantes.error} />

      {faltantes.data && pendentes.length === 0 && (
        <p className="muted">
          {lista.length === 0 ? 'Nenhum item cadastrado ainda.' : 'Tudo confirmado! Não falta nada.'}
        </p>
      )}

      {pendentes.length > 0 && (
        <ul className="faltantes">
          {pendentes.map((f) => {
            const pct = Math.min(100, Math.round((f.quantidadeConfirmada / f.quantidadeNecessaria) * 100));
            return (
              <li key={f.itemId}>
                <div className="faltante-linha">
                  <span>
                    {f.nome} <span className="muted small">({f.categoriaNome})</span>
                    {f.obrigatorio && <span className="tag">obrigatório</span>}
                  </span>
                  <strong>faltam {f.faltam}</strong>
                </div>
                <div
                  className="progress"
                  role="progressbar"
                  aria-label={`${f.nome}: ${f.quantidadeConfirmada} de ${f.quantidadeNecessaria}`}
                  aria-valuenow={f.quantidadeConfirmada}
                  aria-valuemin={0}
                  aria-valuemax={f.quantidadeNecessaria}
                >
                  <div className="progress-bar" style={{ width: `${pct}%` }} />
                </div>
                <span className="muted small">
                  {f.quantidadeConfirmada} de {f.quantidadeNecessaria} confirmados
                </span>
              </li>
            );
          })}
        </ul>
      )}

      {copia === 'falhou' && (
        <div className="copy-fallback">
          <p className="small">Não consegui copiar automaticamente. Selecione o texto abaixo e copie:</p>
          <textarea readOnly rows={8} value={resumo} aria-label="Resumo para WhatsApp" />
        </div>
      )}
    </section>
  );
}
