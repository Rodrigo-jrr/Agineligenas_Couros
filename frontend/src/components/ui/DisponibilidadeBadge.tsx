interface DisponibilidadeBadgeProps {
  disponivel: boolean
}

export function DisponibilidadeBadge({ disponivel }: DisponibilidadeBadgeProps) {
  if (disponivel) {
    return (
      <span className="inline-flex items-center gap-1.5 rounded-full bg-green-100 px-3 py-1 text-xs font-semibold text-green-800">
        <span className="h-1.5 w-1.5 rounded-full bg-green-600" />
        Em estoque
      </span>
    )
  }

  return (
    <span className="inline-flex items-center gap-1.5 rounded-full bg-beige-200 px-3 py-1 text-xs font-semibold text-brown-700">
      <span className="h-1.5 w-1.5 rounded-full bg-brown-500" />
      Indisponivel
    </span>
  )
}
