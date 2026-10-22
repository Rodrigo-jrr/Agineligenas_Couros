import { Package } from 'lucide-react'
import { DisponibilidadeBadge } from '../ui/DisponibilidadeBadge'
import { formatarPreco } from '../../utils/format'
import type { Produto } from '../../types/produto'

interface ProductCardProps {
  produto: Produto
}

export function ProductCard({ produto }: ProductCardProps) {
  return (
    <article className="flex flex-col rounded-2xl border border-beige-300 bg-white/60 p-6 shadow-sm transition-shadow hover:shadow-md">
      <div className="mb-4 flex h-32 items-center justify-center rounded-xl bg-beige-100">
        <Package size={40} className="text-tan-500" strokeWidth={1.5} />
      </div>

      <div className="mb-3 flex items-start justify-between gap-3">
        <h3 className="font-display text-lg font-semibold text-brown-900">
          {produto.nome}
        </h3>
        <DisponibilidadeBadge disponivel={produto.disponivel} />
      </div>

      <p className="mb-4 flex-1 text-sm leading-relaxed text-brown-800/80">
        {produto.descricao ?? 'Descricao a confirmar.'}
      </p>

      <p className="text-base font-semibold text-brown-900">
        {formatarPreco(produto.preco, produto.unidadeMedida)}
      </p>
    </article>
  )
}
