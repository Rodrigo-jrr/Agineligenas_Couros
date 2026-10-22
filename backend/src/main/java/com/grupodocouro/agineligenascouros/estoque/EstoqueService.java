package com.grupodocouro.agineligenascouros.estoque;

import com.grupodocouro.agineligenascouros.common.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class EstoqueService {

    private final EstoqueRepository estoqueRepository;

    public EstoqueService(EstoqueRepository estoqueRepository) {
        this.estoqueRepository = estoqueRepository;
    }

    /**
     * {@code id} e o id do produto (nao o id interno da linha de estoque),
     * ja que e assim que os consumidores da API conhecem o recurso - nao ha
     * nenhum outro endpoint que exponha o id proprio de estoque.
     */
    public EstoqueDTO buscarPorProdutoId(Long produtoId) {
        return estoqueRepository.findByProdutoId(produtoId)
            .map(EstoqueDTO::from)
            .orElseThrow(() -> new ResourceNotFoundException("Estoque nao encontrado para o produto: " + produtoId));
    }
}
