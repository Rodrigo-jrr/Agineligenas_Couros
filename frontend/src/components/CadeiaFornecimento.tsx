import { Section } from './layout/Section'

const ETAPAS = [
  {
    numero: '01',
    titulo: 'Origem',
    texto: 'Materia-prima selecionada junto a fornecedores parceiros.',
  },
  {
    numero: '02',
    titulo: 'Processamento',
    texto: 'Couro tratado e preparado conforme padrao de qualidade.',
  },
  {
    numero: '03',
    titulo: 'Controle de qualidade',
    texto: 'Verificacao antes da disponibilizacao no catalogo.',
  },
  {
    numero: '04',
    titulo: 'Comercializacao',
    texto: 'Produto disponivel para negociacao com nossos clientes.',
  },
]

export function CadeiaFornecimento() {
  return (
    <Section
      id="cadeia"
      eyebrow="Rastreabilidade"
      title="Cadeia de fornecimento"
      description="Acompanhamos o couro desde a origem ate a comercializacao, buscando transparencia em cada etapa do processo."
    >
      <div className="grid grid-cols-1 gap-6 sm:grid-cols-2 lg:grid-cols-4">
        {ETAPAS.map(({ numero, titulo, texto }, indice) => (
          <div key={numero} className="relative">
            <div className="flex items-center gap-4">
              <span className="font-display text-3xl font-semibold text-tan-400">
                {numero}
              </span>
              {indice < ETAPAS.length - 1 && (
                <span className="hidden h-px flex-1 bg-beige-300 lg:block" />
              )}
            </div>
            <h3 className="font-display mt-4 text-lg font-semibold text-brown-900">
              {titulo}
            </h3>
            <p className="mt-2 text-sm leading-relaxed text-brown-800/80">
              {texto}
            </p>
          </div>
        ))}
      </div>
    </Section>
  )
}
