import { ArrowRight, Leaf, ShieldCheck, Truck } from 'lucide-react'
import { Container } from './layout/Container'
import { Button } from './ui/Button'

const DESTAQUES = [
  { icon: ShieldCheck, texto: 'Qualidade verificada em cada lote' },
  { icon: Leaf, texto: 'Origem rastreavel, do curtume ao cliente' },
  { icon: Truck, texto: 'Relacionamento comercial direto' },
]

export function Hero() {
  return (
    <section id="topo" className="relative overflow-hidden bg-brown-900 text-beige-100">
      <div
        aria-hidden
        className="pointer-events-none absolute inset-0 opacity-20"
        style={{
          backgroundImage:
            'radial-gradient(circle at 20% 20%, var(--color-tan-400) 0%, transparent 45%), radial-gradient(circle at 85% 0%, var(--color-brown-500) 0%, transparent 50%)',
        }}
      />

      <Container className="relative py-24 sm:py-32">
        <div className="max-w-2xl">
          <p className="mb-4 text-sm font-semibold tracking-wide text-tan-300 uppercase">
            Couro Grupo do Couro
          </p>
          <h1 className="font-display text-4xl font-semibold text-beige-100 sm:text-5xl lg:text-6xl">
            Couro de qualidade, direto de quem entende do oficio.
          </h1>
          <p className="mt-6 text-lg leading-relaxed text-beige-200/90">
            Produzimos e comercializamos couro para confeccao, calcados e
            industria, com confianca e transparencia em cada etapa da cadeia
            de fornecimento.
          </p>

          <div className="mt-10 flex flex-wrap gap-4">
            <a href="#catalogo">
              <Button variant="primary" className="shadow-lg shadow-black/20">
                Ver catalogo
                <ArrowRight size={18} />
              </Button>
            </a>
            <a href="#contato">
              <Button variant="secondary" className="!border-beige-100/30 !text-beige-100 hover:!bg-beige-100/10">
                Falar com a equipe
              </Button>
            </a>
          </div>

          <dl className="mt-14 grid grid-cols-1 gap-6 border-t border-beige-100/15 pt-8 sm:grid-cols-3">
            {DESTAQUES.map(({ icon: Icon, texto }) => (
              <div key={texto} className="flex items-start gap-3">
                <Icon size={20} className="mt-0.5 shrink-0 text-tan-300" />
                <dt className="sr-only">Destaque</dt>
                <dd className="text-sm text-beige-200/90">{texto}</dd>
              </div>
            ))}
          </dl>
        </div>
      </Container>
    </section>
  )
}
