import { Container } from './layout/Container'

const LINKS = [
  { href: '#catalogo', label: 'Catalogo' },
  { href: '#comercial', label: 'Informacoes comerciais' },
  { href: '#fornecedores', label: 'Fornecedores' },
  { href: '#cadeia', label: 'Cadeia de fornecimento' },
  { href: '#contato', label: 'Contato' },
]

const ANO_ATUAL = new Date().getFullYear()

export function Footer() {
  return (
    <footer className="border-t border-beige-100/10 bg-ink-950 text-beige-200/80">
      <Container className="flex flex-col gap-8 py-12 sm:flex-row sm:items-start sm:justify-between">
        <div>
          <p className="font-display text-lg font-semibold text-beige-100">
            Grupo do Couro
          </p>
          <p className="mt-2 max-w-xs text-sm leading-relaxed">
            Couro de qualidade com rastreabilidade e relacionamento comercial
            transparente.
          </p>
        </div>

        <nav className="flex flex-col gap-2 sm:items-end">
          {LINKS.map((link) => (
            <a
              key={link.href}
              href={link.href}
              className="text-sm transition-colors hover:text-tan-300"
            >
              {link.label}
            </a>
          ))}
        </nav>
      </Container>

      <Container className="border-t border-beige-100/10 py-6 text-xs">
        <p>&copy; {ANO_ATUAL} Grupo do Couro. Todos os direitos reservados.</p>
      </Container>
    </footer>
  )
}
