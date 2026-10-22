-- Seed do catálogo inicial do Grupo do Couro.
--
-- categoria_id é NOT NULL em produto, mas a taxonomia real de categorias
-- ainda não foi definida pelo dono do projeto. Para não inventar uma
-- categoria de negócio e ainda satisfazer a constraint, criamos uma
-- categoria placeholder "Não classificado" — ela é uma necessidade
-- estrutural, não um dado de negócio. Deve ser revisada/substituída
-- quando a taxonomia definitiva existir (ver pendência no README).
--
-- preco, descricao e unidade_medida ficam NULL: não foram informados e
-- não devem ser inventados.
INSERT INTO categoria (nome, descricao) VALUES
    ('Não classificado', 'Categoria temporária para produtos ainda sem classificação definida. Revisar quando a taxonomia real for definida.');

INSERT INTO produto (nome, descricao, preco, unidade_medida, categoria_id, fornecedor_id, ativo)
SELECT 'Couro malhado', NULL, NULL, NULL, c.id, NULL, TRUE
FROM categoria c WHERE c.nome = 'Não classificado';

INSERT INTO produto (nome, descricao, preco, unidade_medida, categoria_id, fornecedor_id, ativo)
SELECT 'Couro maromba', NULL, NULL, NULL, c.id, NULL, TRUE
FROM categoria c WHERE c.nome = 'Não classificado';

INSERT INTO produto (nome, descricao, preco, unidade_medida, categoria_id, fornecedor_id, ativo)
SELECT 'Couro aginelígena', NULL, NULL, NULL, c.id, NULL, TRUE
FROM categoria c WHERE c.nome = 'Não classificado';

INSERT INTO produto (nome, descricao, preco, unidade_medida, categoria_id, fornecedor_id, ativo)
SELECT 'Couro Arthur', NULL, NULL, NULL, c.id, NULL, TRUE
FROM categoria c WHERE c.nome = 'Não classificado';

INSERT INTO estoque (produto_id, quantidade_disponivel)
SELECT p.id, 1 FROM produto p WHERE p.nome = 'Couro malhado';

INSERT INTO estoque (produto_id, quantidade_disponivel)
SELECT p.id, 3 FROM produto p WHERE p.nome = 'Couro maromba';

INSERT INTO estoque (produto_id, quantidade_disponivel)
SELECT p.id, 5 FROM produto p WHERE p.nome = 'Couro aginelígena';

INSERT INTO estoque (produto_id, quantidade_disponivel)
SELECT p.id, 1 FROM produto p WHERE p.nome = 'Couro Arthur';
