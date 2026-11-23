cat > README.md <<'EOF'
# Desafio Sulwork

Sistema para organizar os eventos de alimentação da Sulwork. O organizador cria o evento e o que precisa, os participantes se inscrevem e dizem o que vão levar, e todo mundo vê o que ainda falta.

- Aplicação: https://desafio-sulwork-ten.vercel.app
- Swagger: https://desafio-sulwork-5vnh.onrender.com/swagger-ui.html

O backend está no plano grátis do Render. Se ficar parado, ele dorme e o primeiro acesso pode levar cerca de 1 minuto.

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

O que os testes cobrem:
- Unitários: transições de status, regras de evento, item, inscrição, CPF, contribuição (status, prazo, soma, inscrito), metas, cobertura e alérgenos.
- Integração: fluxo de contribuição pela API, formato dos erros (400, 404, 422) e duas contribuições ao mesmo tempo no mesmo item.
- Frontend: regras de status, resumo do WhatsApp, filtros da lista de eventos e as telas de contribuição, metas, cobertura, inscritos e alérgenos.

## Arquitetura

Backend em camadas: `controller` (HTTP), `service` (regras), `repository` (banco), `domain` (entidades), `dto` (entrada e saída) e `exception` (erros num formato só: 400 validação, 404 não encontrado, 409 conflito no banco, 422 regra de negócio). O frontend fica em `frontend/`.

Padrões usados: camadas (controller, service, repository), DTO com `from()` para não expor a entidade, Repository do Spring Data, tratamento de erro centralizado com `@RestControllerAdvice` e a regra de transição dentro do próprio enum `StatusEvento`.

## Decisões

**Status:** RASCUNHO → ABERTO → ENCERRADO → REALIZADO, e qualquer um pode ir para CANCELADO. Não tem volta.

| | Itens e metas | Inscrição | Contribuição |
| --- | --- | --- | --- |
| RASCUNHO | sim | não | não |
| ABERTO | sim | sim | sim, até o prazo |
| ENCERRADO, REALIZADO, CANCELADO | não | não | não |

**Prazo x status:** contribuição só com o evento ABERTO e antes do prazo. Passar do prazo não muda o status sozinho, só bloqueia as contribuições.

**Contribuições:** só quem está inscrito contribui. A soma de um item não passa da quantidade necessária. Se duas pessoas contribuem no mesmo item ao mesmo tempo, o item fica travado no banco (`SELECT ... FOR UPDATE`) até a primeira terminar, então a segunda já soma a quantidade dela.

**Participantes:** CPF validado e sem repetir. Ninguém se inscreve duas vezes no mesmo evento. Se dois cadastros iguais chegarem juntos, o banco barra o segundo e a API devolve 409.

**Categorias e metas:** a categoria é a mesma para todos os eventos. A meta é por evento (ex.: Bebidas 100%).

## Ambiguidades

- Tipo do evento virou uma lista fixa: café da manhã, almoço, lanche, confraternização e outro.
- Restrições e alérgenos são texto livre separado por vírgula, para não complicar o cadastro.

## Limitações

- Não tem login. Qualquer pessoa que abre o sistema pode criar e alterar.
- Ainda não dá para editar ou excluir evento, participante, categoria e item, só criar. Contribuição e meta já dá para alterar e remover.
- As tabelas são criadas pelo Hibernate (`ddl-auto=update`). Para produção eu usaria migrações com Flyway.
- O horário segue o fuso America/Recife, definido no Dockerfile.

## Cobertura

- Item: conta o que foi confirmado até o necessário. A sobra não conta.
- Categoria: total coberto dividido pelo total necessário. Mostra se bateu a meta.
- Geral: item obrigatório vale o dobro do opcional.
- O evento fica "pronto" quando todos os obrigatórios estão completos e todas as metas foram batidas.

## Restrições e alérgenos

O sistema compara os alérgenos dos itens com as restrições dos inscritos, sem ligar para acento ou maiúscula. Se bater, aparece um aviso na tela do evento. É só aviso, não bloqueia nada.

Na tela do evento, a restrição de cada inscrito aparece embaixo do nome, e na hora de contribuir o item já mostra o que ele contém (ex.: "Bolo (contém glúten, ovo)").

## Feature livre: resumo para o WhatsApp

**Problema:** a combinação do evento acontece no grupo do WhatsApp, e alguém sempre precisa digitar a lista do que falta.

**Como funciona:** no painel "O que falta" tem um botão que copia um texto pronto com o evento, o prazo e os itens que faltam (obrigatórios primeiro), para colar no grupo. Se o navegador não deixar copiar, o texto aparece na tela.

**Decisões:** fica só no frontend, porque usa dados que a tela já tem. O texto é montado por uma função separada (`domain/resumoWhatsapp.ts`), que tem teste próprio.

## Uso de IA

Usei o Claude como apoio para propor a estrutura, escrever boa parte do código e dos testes e tirar dúvidas de Spring. Eu defini o escopo e as prioridades, tomei as decisões de regra de negócio e testei cada parte antes de commitar.

- Sugestão aceita: usar SQL nativo para os itens que faltam e para a cobertura, e deixar a regra do cálculo em Java para dar para testar.
- Mudei de ideia: eu queria usar H2 nos testes, mas com SQL nativo o H2 podia esconder erro. Fiquei no Postgres, o mesmo banco da aplicação.
- Decidi eu: os status do evento, os tipos de evento, categoria como tabela, o uso de Lombok e o que entrava no escopo.
  EOF
  head -3 README.md