import { ClipboardList, HandCoins, MessageCircle } from 'lucide-react'
import { Section } from './layout/Section'

const ETAPAS = [
  {
    icon: MessageCircle,
    titulo: 'Contato comercial',
    texto:
      'Voce entra em contato pelos canais disponiveis e apresenta sua necessidade de couro.',
  },
  {
    icon: ClipboardList,
    titulo: 'Proposta personalizada',
    texto:
      'Nossa equipe avalia disponibilidade e monta uma proposta de acordo com volume e aplicacao.',
  },
  {
    icon: HandCoins,
    titulo: 'Fechamento e entrega',
    texto:
      'Condicoes comerciais e logistica sao combinadas diretamente com nossa equipe antes do fechamento.',
  },
]

export function ComercialInfo() {
  return (
    <Section
      id="comercial"
      eyebrow="Como funciona"
      title="Informacoes comerciais"
      description="Atendemos de forma direta e personalizada. Os detalhes de preco, volume e prazo sao definidos em conversa com nossa equipe comercial."
    >
      <div className="grid grid-cols-1 gap-8 sm:grid-cols-3">
        {ETAPAS.map(({ icon: Icon, titulo, texto }) => (
          <div key={titulo} className="flex flex-col items-start gap-3">
            <div className="flex h-12 w-12 items-center justify-center rounded-full bg-beige-200 text-brown-700">
              <Icon size={22} />
            </div>
            <h3 className="font-display text-lg font-semibold text-brown-900">
              {titulo}
            </h3>
            <p className="text-sm leading-relaxed text-brown-800/80">
              {texto}
            </p>
          </div>
        ))}
      </div>
    </Section>
  )
}
