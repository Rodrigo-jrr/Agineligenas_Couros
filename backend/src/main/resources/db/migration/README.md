# Migrations — Grupo do Couro (AgroGestão)

Flyway, MySQL 8, InnoDB, `utf8mb4_0900_ai_ci`. Rodar em ordem: V1 → V6.

## Tabelas

| Migration | Tabela | Descrição |
|---|---|---|
| V1 | `categoria` | `id`, `nome` (único), `descricao` |
| V2 | `fornecedor` | `id`, `nome`, `tipo` (`INTERNO`\|`VACAS`, via CHECK), `referencia_externa_id` (nullable, link com a API das Vacas) |
| V3 | `produto` | `id`, `nome`, `descricao`, `preco` DECIMAL(10,2), `unidade_medida`, `categoria_id` (FK, obrigatório), `fornecedor_id` (FK, opcional), `ativo` |
| V4 | `estoque` | `id`, `produto_id` (FK 1:1 única), `quantidade_disponivel`, `atualizado_em` |
| V5 | seed | Categoria placeholder + 4 produtos do catálogo inicial + estoque |
| V6 | ajuste de seed | Categorização real dos 4 produtos (Categoria A–D), remoção do placeholder "Não classificado", redistribuição de `quantidade_disponivel` |

## Decisões de modelagem

- **DECIMAL(10,2) para `preco`** — nunca FLOAT/DOUBLE, para evitar erro de arredondamento em valor monetário.
- **`categoria_id` NOT NULL, `fornecedor_id` NULL** — todo produto pertence a uma categoria; fornecedor pode ainda não estar definido (ex.: produto cadastrado antes de linkar com fornecedor interno/Vacas).
- **ON DELETE RESTRICT em `produto.categoria_id`** — não permite apagar categoria em uso, evita produto órfão.
- **ON DELETE SET NULL em `produto.fornecedor_id`** — remover um fornecedor não deve apagar produtos, apenas desvincular.
- **ON DELETE CASCADE em `estoque.produto_id`** — por requisito explícito, estoque nunca pode ficar órfão de produto; ao apagar o produto, o registro de estoque é removido junto.
- **UNIQUE em `estoque.produto_id`** — garante a relação 1:1 entre produto e estoque.
- **CHECK `tipo IN ('INTERNO','VACAS')`** em `fornecedor` — optei por VARCHAR + CHECK em vez de ENUM nativo do MySQL para manter o mapeamento simples no lado JPA (enum Java + `@Enumerated(EnumType.STRING)`) sem acoplar ao ENUM proprietário do MySQL.
- **Índice em `fornecedor.referencia_externa_id`** — não é UNIQUE (ver pendência abaixo), mas tem índice porque a sincronização com a API das Vacas deve consultar por esse campo com frequência.
- **`BIGINT UNSIGNED`** para todas as PKs/FKs — ids nunca são negativos; mapeia sem problema para `Long` no JPA.
- **CHECK de não-negatividade** em `estoque.quantidade_disponivel` e `produto.preco` — integridade garantida pelo banco, não só pela aplicação.

## Seed do catálogo inicial

4 produtos (`Couro malhado`, `Couro maromba`, `Couro aginelígena`, `Couro Arthur`) com `preco`, `descricao` e `unidade_medida` **NULL** — nenhum desses valores foi informado e não foram inventados. As quantidades (1, 3, 5, 1) foram gravadas em `estoque.quantidade_disponivel`, com o significado de "quantidade" (estoque atual vs. aquisição) ainda em aberto — ver pendências.

~~Os 4 produtos do seed foram associados a uma categoria placeholder **"Não classificado"**, criada apenas para satisfazer a constraint `NOT NULL` de `categoria_id` sem inventar uma taxonomia de negócio.~~ **Atualizado em V6**: ver seção "Histórico de resolução" abaixo.

## Histórico de resolução (V6)

O dono do projeto resolveu duas pendências:

- **Categorização**: pediu categorias genéricas (não taxonomia de negócio real — decisão explícita dele usar letras), então V6 criou `Categoria A`, `Categoria B`, `Categoria C`, `Categoria D` e reatribuiu `categoria_id` dos 4 produtos:
  `Couro malhado` → A, `Couro maromba` → B, `Couro aginelígena` → C, `Couro Arthur` → D.
  A categoria placeholder `"Não classificado"` ficou sem produto apontando pra ela e foi removida (o `ON DELETE RESTRICT` do FK permite o `DELETE` quando não há mais referência).
- **Atribuição de `quantidade_disponivel`**: o dono do projeto pediu pra sortear os mesmos 4 números (1, 3, 5, 1) entre os produtos, sem manter a correspondência 1:1 da ordem original. Sorteio feito programaticamente; resultado aplicado em V6:
  `Couro malhado` → 1, `Couro maromba` → 5, `Couro aginelígena` → 3, `Couro Arthur` → 1.

`preco`, `descricao` e `unidade_medida` continuam `NULL` — não foram informados, não foram inventados.

## Pendências para decisão do dono do projeto (bloqueiam ou afetam a Etapa 2 — Back End)

1. ~~**Taxonomia de categorias**~~ — **RESOLVIDA em V6** (categorias genéricas A–D, decisão explícita do dono do projeto; não é taxonomia de negócio real, pode ser revista depois).
2. **`preco`, `descricao` e `unidade_medida` dos 4 produtos do seed** — continuam NULL. Preciso dos valores reais para popular corretamente (e o Back End deve estar preparado para esses campos virem NULL até lá).
3. **Semântica de `estoque.quantidade_disponivel`** — a atribuição específica de números a produtos foi definida (ver V6), mas o significado do número em si (estoque atual disponível para venda vs. quantidade adquirida/recebida) **continua em aberto**. Isso pode afetar regras futuras de `Pedido`/`MovimentacaoEstoque` (fora do escopo desta etapa).
4. **Unicidade de `fornecedor.referencia_externa_id`** — hoje não há constraint UNIQUE porque não ficou claro se pode haver duplicidade (ex.: reprocessamento de sync). Se a API das Vacas garante id externo único por fornecedor, recomendo adicionar `UNIQUE (referencia_externa_id)` filtrado por `tipo = 'VACAS'` numa migration futura.

## Impacto esperado no Back End

- Nomes de tabela/coluna acima são o contrato para as entidades JPA (`Categoria`, `Fornecedor`, `Produto`, `Estoque`) — convenção snake_case compatível com o naming strategy padrão do Spring Boot/Hibernate.
- Repositories que consultam produto por categoria/fornecedor podem contar com os índices automáticos criados pelas FKs (InnoDB indexa FK automaticamente).
- Campos `preco`, `descricao`, `unidade_medida` e `categoria_id` (para os produtos do seed) podem vir NULL/placeholder — o Back End não deve assumir que estão sempre preenchidos até a pendência 1 e 2 serem resolvidas.
