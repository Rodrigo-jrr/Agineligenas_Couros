import { Section } from '../layout/Section'
import { Button } from '../ui/Button'
import { useProdutos } from '../../hooks/useProdutos'
import { CatalogoError, CatalogoVazio } from './CatalogoStates'
import { ProductCard } from './ProductCard'
import { ProductCardSkeleton } from './ProductCardSkeleton'

export function Catalogo() {
  const {
    produtos,
    status,
    errorMessage,
    temMaisPaginas,
    carregandoMais,
    carregarMais,
    recarregar,
  } = useProdutos()

  return (
    <Section
      id="catalogo"
      eyebrow="Nosso catalogo"
      title="Couro para cada etapa da sua producao"
      description="Produtos disponiveis para negociacao comercial. Valores e disponibilidade sao atualizados diretamente do nosso sistema."
    >
      {status === 'loading' && (
        <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
          {Array.from({ length: 4 }).map((_, indice) => (
            <ProductCardSkeleton key={indice} />
          ))}
        </div>
      )}

      {status === 'error' && (
        <CatalogoError
          mensagem={errorMessage ?? 'Erro desconhecido.'}
          aoTentarNovamente={recarregar}
        />
      )}

      {status === 'success' && produtos.length === 0 && <CatalogoVazio />}

      {status === 'success' && produtos.length > 0 && (
        <>
          <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
            {produtos.map((produto) => (
              <ProductCard key={produto.id} produto={produto} />
            ))}
          </div>

          {temMaisPaginas && (
            <div className="mt-10 flex justify-center">
              <Button
                variant="secondary"
                onClick={carregarMais}
                disabled={carregandoMais}
              >
                {carregandoMais ? 'Carregando...' : 'Carregar mais'}
              </Button>
            </div>
          )}
        </>
      )}
    </Section>
  )
}
