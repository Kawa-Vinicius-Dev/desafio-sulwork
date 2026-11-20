import type { Evento, Faltante } from '../api/types';
import { TIPO_LABEL } from './evento';
import { formatarDataHora } from './formatadores';

/**
 * Monta o texto que o organizador cola no grupo do WhatsApp.
 * Usa *negrito* e _itálico_ no formato do próprio WhatsApp.
 */
export function montarResumoWhatsapp(evento: Evento, faltantes: Faltante[]): string {
  const pendentes = faltantes
    .filter((f) => f.faltam > 0)
    .sort((a, b) => Number(b.obrigatorio) - Number(a.obrigatorio) || a.nome.localeCompare(b.nome));

  const linhas = [
    `*${evento.nome}* (${TIPO_LABEL[evento.tipo]})`,
    `Quando: ${formatarDataHora(evento.dataHora)}`,
    `Contribuições até: ${formatarDataHora(evento.prazoAlteracoes)}`,
    '',
  ];

  if (pendentes.length === 0) {
    linhas.push('Tudo confirmado! Não falta nenhum item.');
    return linhas.join('\n');
  }

  linhas.push('*O que ainda falta:*');
  for (const f of pendentes) {
    const obrigatorio = f.obrigatorio ? ' _(obrigatório)_' : '';
    linhas.push(
      `• ${f.nome} (${f.categoriaNome}): faltam ${f.faltam} de ${f.quantidadeNecessaria}${obrigatorio}`,
    );
  }
  linhas.push('', 'Quem puder trazer, confirma no sistema. Obrigado!');
  return linhas.join('\n');
}
