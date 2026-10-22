package com.grupodocouro.agineligenascouros.produto;

import com.grupodocouro.agineligenascouros.common.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Page<ProdutoDTO> listar(Long categoriaId, Pageable pageable) {
        return produtoRepository.buscar(categoriaId, pageable).map(ProdutoDTO::from);
    }

    public ProdutoDTO buscarPorId(Long id) {
        return produtoRepository.buscarPorIdComEstoque(id)
            .map(ProdutoDTO::from)
            .orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado: " + id));
    }
}
