# Sulwork - Frontend

Tela web que cria eventos (café da manhã, almoço, confraternização), inscreve participantes e registra o que cada um vai levar.

Feito com React, TypeScript e Vite. Testes com Vitest.

## Como rodar

Precisa do Node 20+ e do backend na porta 8080.

```bash
cd frontend
npm install
npm run dev
```

http://localhost:5173

Chamadas `/api` repassadas pro backend pelo Vite (`vite.config.ts`).

## Telas

- `/` - lista de eventos e cadastro de evento
- `/eventos/:id` - detalhes do evento: mudar status, inscrever participantes, adicionar itens, registrar contribuições
- `/participantes` - cadastro de participantes
- `/categorias` - cadastro de categorias

## Funcionalidade extra: resumo para WhatsApp

No painel "O que falta" tem um botão que copia um texto pronto pra colar no grupo, com o evento e os itens que ainda faltam.

## Pastas

```
src/
  api/          chamadas para o backend
  domain/       regras de status, formatação de data e CPF, texto do WhatsApp
  hooks/        carregar dados e enviar formulários
  components/   componentes reaproveitados (campo, erro, status)
  pages/        uma tela por arquivo
  test/         configuração dos testes
```

## Erros / Exceptions

Os erros que o backend devolve aparecem na tela: a mensagem geral em cima do formulário e o erro de cada campo. As regras de negócio são validadas no backend. O front esconde os botões que não fazem sentido no status atual.

## Testes

Tem testes do cliente da API, das regras de status, do texto do WhatsApp, da tela de eventos e do painel "O que falta".
