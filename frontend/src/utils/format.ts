const formatadorMoeda = new Intl.NumberFormat('pt-BR', {
  style: 'currency',
  currency: 'BRL',
})

export function formatarPreco(preco: number | undefined, unidadeMedida?: string): string {
  if (preco === undefined) return 'Preco a confirmar'

  const valor = formatadorMoeda.format(preco)
  return unidadeMedida ? `${valor} / ${unidadeMedida}` : valor
}
