import { Handshake, ShieldCheck, Users } from 'lucide-react'
import { Section } from './layout/Section'

const PONTOS = [
  {
    icon: Users,
    titulo: 'Rede selecionada',
    texto:
      'Trabalhamos com fornecedores avaliados, buscando consistencia de qualidade em cada lote recebido.',
  },
  {
    icon: ShieldCheck,
    titulo: 'Padrao de qualidade',
    texto:
      'Cada fornecedor segue criterios internos antes de integrar nossa cadeia de produtos.',
  },
  {
    icon: Handshake,
    titulo: 'Relacionamento de longo prazo',
    texto:
      'Buscamos parcerias duradouras, com comunicacao transparente sobre volume e qualidade.',
  },
]

export function Fornecedores() {
  return (
    <Section
      id="fornecedores"
      tone="dark"
      eyebrow="Nossos parceiros"
      title="Fornecedores"
      description="Construimos nosso catalogo a partir de uma rede de fornecedores comprometidos com qualidade e regularidade de fornecimento."
    >
      <div className="grid grid-cols-1 gap-6 sm:grid-cols-3">
        {PONTOS.map(({ icon: Icon, titulo, texto }) => (
          <div
            key={titulo}
            className="rounded-2xl border border-beige-100/15 bg-beige-100/5 p-6"
          >
            <Icon size={22} className="mb-4 text-tan-300" />
            <h3 className="font-display mb-2 text-lg font-semibold text-beige-100">
              {titulo}
            </h3>
            <p className="text-sm leading-relaxed text-beige-200/80">
              {texto}
            </p>
          </div>
        ))}
      </div>
    </Section>
  )
}
