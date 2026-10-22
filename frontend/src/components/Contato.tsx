import { Mail, Send } from 'lucide-react'
import { useState, type FormEvent } from 'react'
import { Section } from './layout/Section'
import { Button } from './ui/Button'
import { FormField } from './ui/FormField'

const CONTACT_EMAIL = import.meta.env.VITE_CONTACT_EMAIL

interface FormData {
  nome: string
  email: string
  mensagem: string
}

type FormErrors = Partial<Record<keyof FormData, string>>

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/

function validar(dados: FormData): FormErrors {
  const erros: FormErrors = {}

  if (dados.nome.trim().length < 2) {
    erros.nome = 'Informe seu nome.'
  }
  if (!EMAIL_REGEX.test(dados.email.trim())) {
    erros.email = 'Informe um e-mail valido.'
  }
  if (dados.mensagem.trim().length < 10) {
    erros.mensagem = 'Conte um pouco mais sobre sua necessidade (minimo 10 caracteres).'
  }

  return erros
}

export function Contato() {
  const [dados, setDados] = useState<FormData>({
    nome: '',
    email: '',
    mensagem: '',
  })
  const [erros, setErros] = useState<FormErrors>({})
  const [enviado, setEnviado] = useState(false)

  function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault()

    const errosEncontrados = validar(dados)
    setErros(errosEncontrados)
    if (Object.keys(errosEncontrados).length > 0) {
      setEnviado(false)
      return
    }

    const assunto = encodeURIComponent(`Contato pelo site - ${dados.nome}`)
    const corpo = encodeURIComponent(
      `${dados.mensagem}\n\nNome: ${dados.nome}\nE-mail: ${dados.email}`,
    )
    window.location.href = `mailto:${CONTACT_EMAIL}?subject=${assunto}&body=${corpo}`
    setEnviado(true)
  }

  function handleChange(campo: keyof FormData, valor: string) {
    setDados((atual) => ({ ...atual, [campo]: valor }))
    setEnviado(false)
  }

  return (
    <Section
      id="contato"
      eyebrow="Fale com a gente"
      title="Area de contato"
      description="Preencha o formulario abaixo e abriremos seu cliente de e-mail com a mensagem pronta para envio, ou escreva diretamente para o endereco ao lado."
    >
      <div className="grid grid-cols-1 gap-12 lg:grid-cols-5">
        <form onSubmit={handleSubmit} noValidate className="flex flex-col gap-5 lg:col-span-3">
          <FormField label="Nome" htmlFor="contato-nome" error={erros.nome}>
            <input
              id="contato-nome"
              type="text"
              value={dados.nome}
              onChange={(e) => handleChange('nome', e.target.value)}
              className="rounded-lg border border-beige-300 bg-white/70 px-4 py-2.5 text-sm text-ink-950 outline-none focus:border-tan-500"
              autoComplete="name"
            />
          </FormField>

          <FormField label="E-mail" htmlFor="contato-email" error={erros.email}>
            <input
              id="contato-email"
              type="email"
              value={dados.email}
              onChange={(e) => handleChange('email', e.target.value)}
              className="rounded-lg border border-beige-300 bg-white/70 px-4 py-2.5 text-sm text-ink-950 outline-none focus:border-tan-500"
              autoComplete="email"
            />
          </FormField>

          <FormField label="Mensagem" htmlFor="contato-mensagem" error={erros.mensagem}>
            <textarea
              id="contato-mensagem"
              value={dados.mensagem}
              onChange={(e) => handleChange('mensagem', e.target.value)}
              rows={5}
              className="resize-none rounded-lg border border-beige-300 bg-white/70 px-4 py-2.5 text-sm text-ink-950 outline-none focus:border-tan-500"
            />
          </FormField>

          <Button type="submit" variant="primary" className="self-start">
            Enviar mensagem
            <Send size={16} />
          </Button>

          {enviado && (
            <p className="text-sm font-medium text-green-800">
              Seu cliente de e-mail foi aberto com a mensagem preenchida.
            </p>
          )}
        </form>

        <div className="flex flex-col gap-4 rounded-2xl border border-beige-300 bg-white/60 p-6 lg:col-span-2">
          <h3 className="font-display text-lg font-semibold text-brown-900">
            Contato direto
          </h3>
          <a
            href={`mailto:${CONTACT_EMAIL}`}
            className="flex items-center gap-3 text-sm font-medium text-brown-800 hover:text-tan-500"
          >
            <Mail size={18} />
            {CONTACT_EMAIL}
          </a>
        </div>
      </div>
    </Section>
  )
}
