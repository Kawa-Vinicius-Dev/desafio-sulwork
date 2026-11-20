# Desafio Sulwork

API REST para organização de eventos. Cadastro de eventos e participantes, e registro das contribuições de itens, controlando o que ainda falta.

## Tecnologias
- Java 21 e Spring Boot
- Spring Data JPA e PostgreSQL
- Docker e Docker Compose
- Swagger

## Como executar
```
docker compose up --build
```
Documentação da API: http://localhost:8080/swagger-ui.html

## Principais recursos
- `/api/eventos`: eventos e mudança de status
- `/api/participantes`: participantes
- `/api/categorias`: categorias de itens
- `/api/eventos/{eventoId}/participacoes`: inscrições
- `/api/eventos/{eventoId}/itens`: itens do evento
- `/api/eventos/{eventoId}/faltantes`: itens que ainda faltam

## Testes
```
./mvnw test
```
