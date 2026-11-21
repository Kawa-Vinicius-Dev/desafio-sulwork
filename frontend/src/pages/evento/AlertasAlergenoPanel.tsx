import type { AlertaAlergeno } from '../../api/types';
import { ErrorAlert } from '../../components/ErrorAlert';
import type { Resource } from '../../hooks/useResource';

interface Props {
  alertas: Resource<AlertaAlergeno[]>;
}

/**
 * Avisa o organizador quando um item tem alérgeno que bate com a restrição de algum inscrito.
 * Só aparece se houver alerta, para não poluir a tela.
 */
export function AlertasAlergenoPanel({ alertas }: Props) {
  if (alertas.error) return <ErrorAlert error={alertas.error} />;
  if (!alertas.data || alertas.data.length === 0) return null;

  return (
    <section className="card">
      <h3>Atenção com alérgenos</h3>
      <ul className="faltantes">
        {alertas.data.map((a) => (
          <li key={`${a.itemId}-${a.participanteId}`} className="alert alert-warn">
            <strong>{a.itemNome}</strong> contém {a.alergenos.join(', ')}, e <strong>{a.participanteNome}</strong> tem
            restrição.
          </li>
        ))}
      </ul>
    </section>
  );
}
