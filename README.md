# Integração com HubSpot (Spring Boot)

Este projeto é uma aplicação backend desenvolvida em Java com Spring Boot que realiza a integração com a API da HubSpot, utilizando autenticação OAuth 2.0.

## ✨ Funcionalidades

- 🔐 Geração da URL de autorização OAuth para o HubSpot
- 🔁 Troca do código de autorização pelo access token
- 📇 Criação de contatos no CRM da HubSpot
- 📬 Recebimento de webhooks do tipo `contact.creation`

---

## ✅ Requisitos

- Java 17+
- Maven
- Conta no [HubSpot Developer](https://developers.hubspot.com)
- Ngrok (opcional, para testar webhooks localmente)

---

## 🚀 Como rodar o projeto localmente

### 1. Clone o repositório
```bash
git clone https://github.com/seu-usuario/hubspot-integration.git
cd hubspot-integration
```

### 2. Configure os dados de ambiente
Edite o `application.properties` com suas credenciais do HubSpot:

```properties
hubspot.client-id=SEU_CLIENT_ID
hubspot.client-secret=SEU_CLIENT_SECRET
hubspot.redirect-uri=http://localhost:8080/hubspot/oauth/callback
hubspot.scopes=crm.objects.contacts.write oauth crm.schemas.contacts.read
```

### 3. Rode a aplicação
```bash
./mvnw spring-boot:run
```

A aplicação ficará disponível em `http://localhost:8080`.

---

## 🔄 Fluxo OAuth (autenticação)

1. Acesse `http://localhost:8080/hubspot/oauth/auth`
2. Você será redirecionado para o HubSpot para autorizar o app
3. Após a autorização, o HubSpot redireciona de volta com um `code`
4. O backend troca o código por um `access_token`

---

## 🧪 Testando criação de contatos

Após autenticação:

### POST `/hubspot/contacts`
```json
{
  "email": "exemplo@teste.com",
  "firstname": "Exemplo",
  "lastname": "Teste"
}
```

Resposta: 201 Created

---

## 🔔 Testando Webhooks

1. Use o [ngrok](https://ngrok.com):
```bash
ngrok http 8080
```

2. Pegue a URL gerada, ex: `https://xxxx.ngrok-free.app`
3. No painel do HubSpot Developer, configure o webhook:
    - URL: `https://xxxx.ngrok-free.app/hubspot/webhook`
    - Evento: `contact.creation`
4. Crie um contato e veja os logs recebidos no terminal

---

## 🧪 Testes

Os testes unitários cobrem:
- `ContactService` (criação de contatos)
- `AuthService` (geração de URL e troca de token)

Execute:
```bash
./mvnw test
```

---

## 🔧 Melhorias Futuras

- Armazenar `access_token` e `refresh_token` em banco de dados
- Renovação automática do `access_token`
- Validação de segurança com assinatura HMAC dos webhooks
- Interface web com status dos tokens e contatos criados
- Dockerização do projeto

---
