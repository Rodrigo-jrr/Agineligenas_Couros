import { Menu, X } from 'lucide-react'
import { useState } from 'react'
import { Container } from './layout/Container'

const LINKS = [
  { href: '#catalogo', label: 'Catalogo' },
  { href: '#comercial', label: 'Informacoes comerciais' },
  { href: '#fornecedores', label: 'Fornecedores' },
  { href: '#cadeia', label: 'Cadeia de fornecimento' },
  { href: '#contato', label: 'Contato' },
]

export function Header() {
  const [menuAberto, setMenuAberto] = useState(false)

  return (
    <header className="sticky top-0 z-50 border-b border-beige-300 bg-cream-50/95 backdrop-blur">
      <Container className="flex h-16 items-center justify-between sm:h-20">
        <a href="#topo" className="font-display text-lg font-semibold text-brown-900 sm:text-xl">
          Grupo do Couro
        </a>

        <nav className="hidden items-center gap-8 lg:flex">
          {LINKS.map((link) => (
            <a
              key={link.href}
              href={link.href}
              className="text-sm font-medium text-brown-800 transition-colors hover:text-tan-500"
            >
              {link.label}
            </a>
          ))}
        </nav>

        <a
          href="#contato"
          className="hidden rounded-full bg-brown-900 px-5 py-2.5 text-sm font-semibold text-cream-50 transition-colors hover:bg-brown-800 lg:inline-flex"
        >
          Fale conosco
        </a>

        <button
          type="button"
          aria-label={menuAberto ? 'Fechar menu' : 'Abrir menu'}
          aria-expanded={menuAberto}
          onClick={() => setMenuAberto((aberto) => !aberto)}
          className="inline-flex h-10 w-10 items-center justify-center rounded-full text-brown-900 lg:hidden"
        >
          {menuAberto ? <X size={22} /> : <Menu size={22} />}
        </button>
      </Container>

      {menuAberto && (
        <nav className="border-t border-beige-300 bg-cream-50 lg:hidden">
          <Container className="flex flex-col gap-1 py-4">
            {LINKS.map((link) => (
              <a
                key={link.href}
                href={link.href}
                onClick={() => setMenuAberto(false)}
                className="rounded-lg px-3 py-2.5 text-sm font-medium text-brown-800 hover:bg-beige-100"
              >
                {link.label}
              </a>
            ))}
          </Container>
        </nav>
      )}
    </header>
  )
}
