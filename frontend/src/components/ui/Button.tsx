import type { ButtonHTMLAttributes } from 'react'

type ButtonVariant = 'primary' | 'secondary' | 'ghost'

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant
}

const variantClasses: Record<ButtonVariant, string> = {
  primary:
    'bg-tan-500 text-cream-50 hover:bg-tan-400 focus-visible:outline-tan-500',
  secondary:
    'bg-transparent border border-beige-300 text-ink-950 hover:bg-beige-100 focus-visible:outline-brown-700',
  ghost:
    'bg-transparent text-beige-100 border border-beige-100/30 hover:bg-beige-100/10 focus-visible:outline-beige-100',
}

export function Button({
  variant = 'primary',
  className = '',
  ...props
}: ButtonProps) {
  return (
    <button
      className={`inline-flex items-center justify-center gap-2 rounded-full px-6 py-3 text-sm font-semibold transition-colors focus-visible:outline focus-visible:outline-2 focus-visible:outline-offset-2 disabled:cursor-not-allowed disabled:opacity-60 ${variantClasses[variant]} ${className}`}
      {...props}
    />
  )
}
