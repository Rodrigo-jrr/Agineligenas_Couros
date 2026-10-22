import { useCallback, useEffect, useState } from 'react'
import { ApiError } from '../services/http'
import { listarProdutos } from '../services/produtoService'
import type { Produto } from '../types/produto'

interface UseProdutosState {
  produtos: Produto[]
  status: 'loading' | 'success' | 'error'
  errorMessage: string | null
  temMaisPaginas: boolean
  carregandoMais: boolean
  carregarMais: () => void
  recarregar: () => void
}

const TAMANHO_PAGINA = 20

export function useProdutos(): UseProdutosState {
  const [produtos, setProdutos] = useState<Produto[]>([])
  const [pagina, setPagina] = useState(0)
  const [totalPaginas, setTotalPaginas] = useState(1)
  const [status, setStatus] = useState<'loading' | 'success' | 'error'>(
    'loading',
  )
  const [errorMessage, setErrorMessage] = useState<string | null>(null)
  const [carregandoMais, setCarregandoMais] = useState(false)
  const [tentativa, setTentativa] = useState(0)

  useEffect(() => {
    let cancelado = false

    async function carregarPrimeiraPagina() {
      setStatus('loading')
      setErrorMessage(null)

      try {
        const resposta = await listarProdutos(0, TAMANHO_PAGINA)
        if (cancelado) return

        setProdutos(resposta.content)
        setPagina(0)
        setTotalPaginas(resposta.page.totalPages)
        setStatus('success')
      } catch (error) {
        if (cancelado) return

        setErrorMessage(
          error instanceof ApiError
            ? error.message
            : 'Nao foi possivel carregar o catalogo. Tente novamente.',
        )
        setStatus('error')
      }
    }

    void carregarPrimeiraPagina()

    return () => {
      cancelado = true
    }
  }, [tentativa])

  const carregarMais = useCallback(() => {
    const proximaPagina = pagina + 1
    if (proximaPagina >= totalPaginas || carregandoMais) return

    setCarregandoMais(true)
    listarProdutos(proximaPagina, TAMANHO_PAGINA)
      .then((resposta) => {
        setProdutos((atual) => [...atual, ...resposta.content])
        setPagina(proximaPagina)
        setTotalPaginas(resposta.page.totalPages)
      })
      .catch(() => {
        // Falha ao carregar mais itens: mantem o catalogo ja exibido,
        // o usuario pode tentar novamente clicando em "Carregar mais".
      })
      .finally(() => setCarregandoMais(false))
  }, [pagina, totalPaginas, carregandoMais])

  const recarregar = useCallback(() => setTentativa((n) => n + 1), [])

  return {
    produtos,
    status,
    errorMessage,
    temMaisPaginas: pagina + 1 < totalPaginas,
    carregandoMais,
    carregarMais,
    recarregar,
  }
}
