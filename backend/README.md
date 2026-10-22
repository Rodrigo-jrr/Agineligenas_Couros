# Back End - Agineligenas Couros (Grupo do Couro)

Spring Boot (Maven) para consulta de Produto/Estoque, com autenticacao por API Key.
Schema (Flyway `V1`-`V5`) ja entregue pelo time de banco em
`src/main/resources/db/migration/` - ver o `README.md` daquela pasta para o
contrato de tabelas/colunas e as pendencias conhecidas (categorias, preco/
descricao/unidade_medida NULL nos produtos do seed).

## Rodando localmente

1. Banco de dados (MySQL 8 descartavel, so para dev):

   ```
   docker compose up -d
   ```

   Isso sobe um MySQL 8 em `localhost:3309`, database `grupodocouro`.

2. Variaveis de ambiente (nenhuma credencial fica hardcoded no `application.yml`):

   ```
   export DB_URL="jdbc:mysql://localhost:3309/grupodocouro"
   export DB_USERNAME=grupodocouro
   export DB_PASSWORD=grupodocouro
   export SAPATOS_API_KEY="uma-chave-qualquer-para-dev"
   export VACAS_CONSUMER_API_KEY="outra-chave-qualquer-para-dev"
   # Opcional - default http://localhost:5173 (porta padrao do Vite em dev)
   export FRONTEND_ORIGIN="http://localhost:5173"
   # Opcionais - so necessarios pra rodar a sincronizacao da Etapa 4 (ver secao propria)
   export VACAS_API_BASE_URL="http://192.168.68.78:8000"
   export VACAS_API_KEY="a-chave-que-o-time-das-vacas-forneceu"
   ```

3. Build e testes:

   ```
   mvn verify
   ```

   Os testes (`ProdutoControllerSecurityTest`) sao testes de slice
   (`@WebMvcTest`, service mockado) e nao precisam de Docker/banco - cobrem o
   mecanismo de API Key (401/403/200) e o contrato de resposta. A validacao
   fim a fim contra um MySQL real (migrations + seed) foi feita manualmente
   com o `docker-compose.yml` acima + `curl`.

4. Rodar a aplicacao:

   ```
   mvn spring-boot:run
   ```

   - Swagger UI: http://localhost:8080/swagger-ui.html
   - OpenAPI JSON: http://localhost:8080/v3/api-docs

## Autenticacao

Todos os endpoints em `/api/v1/**` exigem o header `X-API-Key`, **exceto**
`/api/v1/public/**` (ver abaixo). Clientes registrados hoje:

| Cliente | Env var da chave | Authorities |
|---|---|---|
| Sapatos | `SAPATOS_API_KEY` | `PRODUTO_READ`, `ESTOQUE_READ` |
| Vacas | `VACAS_CONSUMER_API_KEY` | `PRODUTO_READ` (sem acesso a estoque) |

`VACAS_CONSUMER_API_KEY` != `VACAS_API_KEY` - esta ultima e reservada pra
Etapa 4, quando NOSSO backend for consumidor da API DELES (direcao oposta do
fluxo); nomes diferentes de proposito, pra nao confundir.

Para registrar um novo cliente basta adicionar uma entrada em
`app.security.clients` no `application.yml` com nome/chave(env var)/
authorities - sem alterar codigo. Ver `common/security/ApiSecurityProperties`.

| Situacao | Status |
|---|---|
| Sem header `X-API-Key` | 401 |
| Header com chave que nao corresponde a nenhum cliente | 401 |
| Chave valida, mas cliente sem a authority exigida pelo recurso | 403 |
| Chave valida e autorizada | 200 |

`common/security/SecurityConfig` define 3 cadeias de filtro avaliadas em
ordem: `/api/v1/public/**` (sem autenticacao) e avaliada ANTES de `/api/v1/**`
(protegida), senao cairia no padrao mais generico.

## Endpoints protegidos (B2B, exigem API Key)

- `GET /api/v1/produtos` - paginavel (`page`, `size`), filtravel por `categoriaId`.
  Resposta paginada no formato DTO estavel do Spring Data (`content` + `page`
  com `size`/`number`/`totalElements`/`totalPages`), em vez de serializar
  `PageImpl` diretamente (que nao tem formato garantido entre versoes).
- `GET /api/v1/produtos/{id}`
- `GET /api/v1/estoque/{id}` - `{id}` e o id do **produto** (nao o id interno da
  linha de `estoque`), ja que nenhum outro endpoint expoe o id proprio de
  estoque.

Campos opcionais (`descricao`, `preco`, `unidadeMedida`, `fornecedorId`) sao
omitidos do JSON quando nulos (nao aparecem como `null`).

## Endpoints publicos (sem autenticacao, catalogo para a landing page)

- `GET /api/v1/public/produtos`
- `GET /api/v1/public/produtos/{id}`

Contrato separado de `/api/v1/produtos` por decisao do orquestrador do projeto:
o frontend publico (landing page React, sem login) nunca pode carregar uma
API Key, e `/api/v1/produtos` continua sendo o contrato B2B protegido para
Sapatos/Vacas, inalterado. `/api/v1/public/**` reaproveita o mesmo
`ProdutoService` (mesma regra de consulta) - sem filtro nem paginacao alem do
basico (sem `categoriaId`, so `page`/`size` default). Documentado no Swagger
sem `securityScheme` (`@SecurityRequirements` vazio por cima do default
global).

**Nao reaproveita o `ProdutoDTO` na resposta.** Sem nenhum controle de
acesso, o numero exato de estoque (`quantidadeDisponivel`) e um dado
comercial sensivel e nao pode ser exposto publicamente. `PublicProdutoController`
converte para `ProdutoPublicoDTO` (`id`, `nome`, `descricao`, `preco`,
`unidadeMedida`, `disponivel`), onde `disponivel` e um booleano calculado
(`quantidadeDisponivel > 0`) - sem revelar a quantidade exata nem o
`fornecedorId`. O numero exato de estoque continua disponivel normalmente em
`GET /api/v1/estoque/{id}`, protegido por API Key, para parceiros B2B
autenticados.

## CORS

Liberado **apenas** para `/api/v1/public/**` - metodo `GET`, sem credentials
(cookies/Authorization), a partir da origem configurada em `FRONTEND_ORIGIN`
(default `http://localhost:5173`, a porta padrao do Vite em dev local). Ver
`SecurityConfig#publicCorsConfigurationSource`. `/api/v1/produtos` e
`/api/v1/estoque` (contrato B2B, chamado backend-to-backend, nunca por
navegador) **nao** tem CORS liberado - nenhuma origem recebe
`Access-Control-Allow-Origin` nesses caminhos.

## Verbo HTTP invalido

`POST`/`PUT`/`DELETE` num recurso que so aceita `GET` retornam `405 Method
Not Allowed` com o `ApiError` padrao (tratado explicitamente em
`GlobalExceptionHandler` como erro de uso do cliente, nao logado como erro
inesperado).

## Versionamento de rota

Toda rota comeca com `/api/v1/`. Rotas antigas sem `v1` (ex.: `/api/produtos`)
nao existem mais - retornam `404` (tratado explicitamente via
`NoResourceFoundException` em `GlobalExceptionHandler`, nao `500`). Swagger UI
e OpenAPI docs ficam fora do versionamento (`/swagger-ui.html`,
`/v3/api-docs`).

## Formato de preco e data (contrato com consumidores)

- `preco`: `BigDecimal` mapeado de `DECIMAL(10,2)` - sempre 2 casas decimais
  fixas no JSON (ex.: `3500.00`, nunca `3500` ou `3500.0`), inclusive com
  zero a direita. Confirmado por teste explicito em
  `ProdutoPrecoSerializationTest`.
- `EstoqueDTO.atualizadoEm`: `OffsetDateTime`, sempre ISO 8601 completo com
  offset (ex.: `2026-10-14T15:30:00-03:00`). A entidade `Estoque.atualizadoEm`
  mapeia a coluna `TIMESTAMP` (sem migration - `@JdbcTypeCode(SqlTypes.TIMESTAMP)`
  mantem o tipo de coluna, so muda o binding JPA) e e convertida para o
  horario de Brasilia (`America/Sao_Paulo`) na camada de DTO. Brasil nao usa
  mais horario de verao (Decreto 10.166/2019), mas a conversao usa `ZoneId`
  (nao offset fixo hardcoded), entao continua correta se isso mudar.
  Confirmado por teste explicito em `EstoqueDTOSerializationTest`.

## Etapa 4 - nosso backend como consumidor da API das Vacas

Sentido inverso de tudo acima: aqui NOSSO backend chama a API deles (pacote
`vacas`), pra manter `Fornecedor` sincronizado com o catalogo de racas delas.

**Contrato delas** (`VACAS_API_BASE_URL`, hoje `http://192.168.68.78:8000`,
rede local do time das Vacas): `GET /api/parceiro/racas` (lista completa, em
ordem alfabetica) e `GET /api/parceiro/racas/{id}` (404 se nao existir ou
nao estiver a venda). Cada raca: `id` (inteiro), `nome` (string),
`quantidade_disponivel` (inteiro, pode ser zero), `preco` (string com 2
casas decimais - **nao** e numero no contrato delas). Autenticacao por
`X-API-Key` (`VACAS_API_KEY`, distinta de `VACAS_CONSUMER_API_KEY` - ver
secao de Autenticacao acima); sem header ou chave invalida -> 401; verbo
diferente de GET -> 405. Formato de erro delas e `{"detail": "..."}`,
diferente do nosso `ApiError`.

**O que sincronizamos:** cada raca vira ou atualiza um `Fornecedor` (tabela
ja existente, ver `V2__create_table_fornecedor.sql`) com `tipo=VACAS`,
`referenciaExternaId` = id da raca (como string) e `nome` = nome da raca.
`preco` e `quantidadeDisponivel` da raca NAO sao usados hoje (ficam no DTO
`RacaVacasDTO` so porque fazem parte do contrato delas) - se um uso futuro
precisar desses campos, o DTO ja os expoe.

**Decisao de design - `preco` como `String`:** o contrato delas manda
`preco` como string (ex. `"150.00"`), nao numero. Como a sincronizacao hoje
nao usa esse valor, optei por manter `RacaVacasDTO.preco()` como `String`
tambem, em vez de parsear pra `BigDecimal` sem necessidade - menos risco de
erro de parsing numa sincronizacao que nem usa o valor. Se um consumidor
futuro precisar do preco como numero, parsear no ponto de uso.

**find-then-upsert, nao upsert via constraint:** `fornecedor.referencia_externa_id`
NAO tem `UNIQUE` constraint no banco (ver README das migrations, pendencia 4
ja registrada pelo time de Banco). `VacasFornecedorSyncService` busca por
`tipo + referenciaExternaId` antes de decidir criar ou atualizar - a
aplicacao e responsavel por evitar duplicata, nao o banco. **Isso nao e
seguro contra concorrencia** (duas sincronizacoes simultaneas podem ambas
decidir "nao existe" e criar 2 fornecedores pra mesma raca) - como e uma
sincronizacao disparada manualmente, nao automatica/concorrente, aceitei
esse risco por ora. Se o projeto evoluir pra sincronizacao concorrente
(scheduler, multiplas instancias), a correcao correta e pedir ao time de
Banco de Dados uma migration adicionando
`UNIQUE (tipo, referencia_externa_id)` - **nao pedir direto ao time de
Banco**, repassar pro Maestro coordenar.

**Robustez:** se uma raca especifica falhar na sincronizacao (banco, dado
invalido, etc.), a falha e logada (`WARN`, sem a chave) e as outras racas
continuam normalmente - ver `VacasFornecedorSyncService.sincronizarFornecedores()`.
Se a chamada de LISTAGEM em si falhar (API fora do ar, rede, etc.), a
sincronizacao inteira e abortada de forma controlada (log `ERROR`, retorna
resultado zerado, nao lanca excecao).

**Como disparar a sincronizacao:** sem scheduler nem cron (desnecessario pro
escopo deste projeto) - e um argumento de linha de comando na inicializacao:

```
mvn spring-boot:run -Dspring-boot.run.arguments=--sync-vacas-fornecedores
# ou com o jar empacotado:
java -jar target/agineligenascouros-backend-0.1.0.jar --sync-vacas-fornecedores
```

Sem essa flag, a aplicacao sobe normalmente e nao faz nada relacionado a
Vacas. Ver `VacasSyncRunner`.

**Testes:** `VacasApiClientTest` usa `MockRestServiceServer` (built-in do
Spring Test, sem dependencia nova) pra simular as respostas HTTP delas, sem
chamada real contra `192.168.68.78` (pode nao estar alcancavel desta
maquina/rede). `VacasFornecedorSyncServiceTest` e um teste de unidade puro
(Mockito, sem contexto Spring) cobrindo criacao, atualizacao e continuidade
apos falha de uma raca especifica.
