import type { StatusEvento } from '../api/types';
import { STATUS_LABEL } from '../domain/evento';

export function StatusBadge({ status }: { status: StatusEvento }) {
  return <span className={`badge badge-${status.toLowerCase()}`}>{STATUS_LABEL[status]}</span>;
}
