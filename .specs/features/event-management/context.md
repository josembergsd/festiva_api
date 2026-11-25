# Contexto: Gerenciamento de eventos

- Consumidor: usuário da API Festiva.
- Entidade principal: evento com título, descrição opcional, início, fim, local, capacidade e status.
- Estados de evento: `SCHEDULED`, `CANCELLED`, `COMPLETED`.
- Acesso: consultas públicas; gravações exigem JWT emitido pelos endpoints de autenticação.
- Fonte funcional: `SKILL_Event_API_Hexagonal.md` e confirmação do escopo MVP pelo consumidor.
- Premissas não especificadas no documento: contas autenticadas têm a mesma permissão de escrita; não há gestão de participantes; o cancelamento é lógico.
