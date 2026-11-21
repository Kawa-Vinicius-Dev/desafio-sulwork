# Desafio Sulwork

Sistema para organizar os eventos de alimentação da Sulwork. O organizador cria o evento e o que precisa, os participantes se inscrevem e dizem o que vão levar, e todo mundo vê o que ainda falta.

- Aplicação: LINK_DO_FRONT - pendente
- Swagger: LINK_DO_BACK/swagger-ui.html - pendente

## Tecnologias

Java 21, Spring Boot, JPA/Hibernate, PostgreSQL, Swagger, React com TypeScript (Vite), Docker Compose. Testes com JUnit 5, Mockito e Vitest.

## Como rodar

```bash
docker compose up --build
```

- Front: http://localhost:3000
- Swagger: http://localhost:8080/swagger-ui.html

## Testes

```bash
docker compose up -d db
./mvnw test

cd frontend
npm install
npm test
```

Os testes de integração rodam no Postgres, não no H2, porque o projeto usa SQL nativo.

## Arquitetura

Backend em camadas: `controller` (HTTP), `service` (regras), `repository` (banco), `domain` (entidades), `dto` (entrada e saída) e `exception` (erros num formato só: 400 validação, 404 não encontrado, 422 regra de negócio). O frontend fica em `frontend/`.

## Decisões

**Status:** RASCUNHO → ABERTO → ENCERRADO → REALIZADO, e qualquer um pode ir para CANCELADO. Não tem volta.

| | Itens e metas | Inscrição | Contribuição |
| --- | --- | --- | --- |
| RASCUNHO | sim | não | não |
| ABERTO | sim | sim | sim, até o prazo |
| ENCERRADO, REALIZADO, CANCELADO | não | não | não |

**Prazo x status:** contribuição só com o evento ABERTO e antes do prazo. Passar do prazo não muda o status sozinho, só bloqueia as contribuições.

**Contribuições:** só quem está inscrito contribui. A soma de um item não passa da quantidade necessária.

**Participantes:** CPF validado e sem repetir. Ninguém se inscreve duas vezes no mesmo evento.

**Categorias e metas:** a categoria é a mesma para todos os eventos. A meta é por evento (ex.: Bebidas 100%).

## Ambiguidades

- Tipo do evento virou uma lista fixa: café da manhã, almoço, lanche, confraternização e outro.
- Restrições e alérgenos são texto livre separado por vírgula, para não complicar o cadastro.

## Cobertura

- Item: conta o que foi confirmado até o necessário. A sobra não conta.
- Categoria: total coberto dividido pelo total necessário. Mostra se bateu a meta.
- Geral: item obrigatório vale o dobro do opcional.
- O evento fica "pronto" quando todos os obrigatórios estão completos e todas as metas foram batidas.

## Restrições e alérgenos

O sistema compara os alérgenos dos itens com as restrições dos inscritos, sem ligar para acento ou maiúscula. Se bater, aparece um aviso na tela do evento. É só aviso, não bloqueia nada.

## Feature livre: resumo para o WhatsApp

A combinação do evento acontece no grupo do WhatsApp. No painel "O que falta" tem um botão que copia um texto pronto com o evento, o prazo e os itens que faltam, para colar no grupo.

## Uso de IA

Usei o Claude como apoio para propor a estrutura, escrever boa parte do código e dos testes e tirar dúvidas de Spring. Eu defini o escopo e as prioridades, tomei as decisões de regra de negócio e testei cada parte antes de commitar.

- Sugeri: usar SQL nativo para os itens que faltam e para a cobertura.
- Mudei de ideia: eu queria usar H2 nos testes, mas com SQL nativo o H2 podia esconder erro. Fiquei no Postgres, o mesmo banco da aplicação.
- Decidi eu: os status do evento, os tipos de evento, categoria como tabela, o uso de Lombok e o que entrava no escopo.