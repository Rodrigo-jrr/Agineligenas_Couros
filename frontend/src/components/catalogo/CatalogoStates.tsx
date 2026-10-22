import { AlertTriangle, PackageSearch } from 'lucide-react'
import { Button } from '../ui/Button'

interface CatalogoErrorProps {
  mensagem: string
  aoTentarNovamente: () => void
}

export function CatalogoError({ mensagem, aoTentarNovamente }: CatalogoErrorProps) {
  return (
    <div className="flex flex-col items-center gap-4 rounded-2xl border border-beige-300 bg-white/60 px-6 py-16 text-center">
      <AlertTriangle size={32} className="text-tan-500" />
      <div>
        <p className="font-semibold text-brown-900">
          Nao foi possivel carregar o catalogo
        </p>
        <p className="mt-1 text-sm text-brown-800/80">{mensagem}</p>
      </div>
      <Button variant="secondary" onClick={aoTentarNovamente}>
        Tentar novamente
      </Button>
    </div>
  )
}

export function CatalogoVazio() {
  return (
    <div className="flex flex-col items-center gap-3 rounded-2xl border border-beige-300 bg-white/60 px-6 py-16 text-center">
      <PackageSearch size={32} className="text-tan-500" />
      <p className="font-semibold text-brown-900">
        Nenhum produto disponivel no momento
      </p>
      <p className="text-sm text-brown-800/80">
        Nosso catalogo esta sendo atualizado. Volte em breve.
      </p>
    </div>
  )
}
