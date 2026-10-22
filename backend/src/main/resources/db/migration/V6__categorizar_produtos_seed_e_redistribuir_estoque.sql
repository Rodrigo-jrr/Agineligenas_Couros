-- Resolve pendencia 1 (taxonomia de categorias) e parte de dados da pendencia 3
-- (quantidade_disponivel) do README. Categorias genericas (Categoria A-D) por
-- decisao explicita do dono do projeto, nao taxonomia de negocio real.
-- preco, descricao, unidade_medida permanecem NULL - nao informados ainda.

INSERT INTO categoria (nome, descricao) VALUES
    ('Categoria A', NULL),
    ('Categoria B', NULL),
    ('Categoria C', NULL),
    ('Categoria D', NULL);

UPDATE produto p JOIN categoria c ON c.nome = 'Categoria A'
    SET p.categoria_id = c.id WHERE p.nome = 'Couro malhado';

UPDATE produto p JOIN categoria c ON c.nome = 'Categoria B'
    SET p.categoria_id = c.id WHERE p.nome = 'Couro maromba';

UPDATE produto p JOIN categoria c ON c.nome = 'Categoria C'
    SET p.categoria_id = c.id WHERE p.nome = 'Couro aginelígena';

UPDATE produto p JOIN categoria c ON c.nome = 'Categoria D'
    SET p.categoria_id = c.id WHERE p.nome = 'Couro Arthur';

-- Placeholder sem produto apontando - RESTRICT permite DELETE agora.
DELETE FROM categoria WHERE nome = 'Não classificado';

-- Redistribuicao sorteada das quantidades (1,3,5,1), nao mantem ordem original.
UPDATE estoque e JOIN produto p ON p.id = e.produto_id
    SET e.quantidade_disponivel = 1 WHERE p.nome = 'Couro malhado';

UPDATE estoque e JOIN produto p ON p.id = e.produto_id
    SET e.quantidade_disponivel = 5 WHERE p.nome = 'Couro maromba';

UPDATE estoque e JOIN produto p ON p.id = e.produto_id
    SET e.quantidade_disponivel = 3 WHERE p.nome = 'Couro aginelígena';

UPDATE estoque e JOIN produto p ON p.id = e.produto_id
    SET e.quantidade_disponivel = 1 WHERE p.nome = 'Couro Arthur';
