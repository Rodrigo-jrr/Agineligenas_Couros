import type { ReactNode } from 'react'
import { Container } from './Container'

interface SectionProps {
  id: string
  eyebrow?: string
  title: string
  description?: string
  children: ReactNode
  tone?: 'light' | 'dark'
  className?: string
}

export function Section({
  id,
  eyebrow,
  title,
  description,
  children,
  tone = 'light',
  className = '',
}: SectionProps) {
  const toneClasses =
    tone === 'dark'
      ? 'bg-brown-900 text-beige-100'
      : 'bg-cream-50 text-ink-950'

  return (
    <section id={id} className={`scroll-mt-20 py-20 sm:py-24 ${toneClasses} ${className}`}>
      <Container>
        <div className="mb-12 max-w-2xl">
          {eyebrow && (
            <p
              className={`mb-3 text-sm font-semibold tracking-wide uppercase ${
                tone === 'dark' ? 'text-tan-300' : 'text-tan-500'
              }`}
            >
              {eyebrow}
            </p>
          )}
          <h2 className="font-display text-3xl font-semibold sm:text-4xl">
            {title}
          </h2>
          {description && (
            <p
              className={`mt-4 text-base leading-relaxed sm:text-lg ${
                tone === 'dark' ? 'text-beige-200/90' : 'text-brown-800/90'
              }`}
            >
              {description}
            </p>
          )}
        </div>
        {children}
      </Container>
    </section>
  )
}
