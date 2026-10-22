const API_BASE_URL = import.meta.env.VITE_API_BASE_URL.replace(/\/+$/, '')

export class ApiError extends Error {
  readonly status?: number

  constructor(message: string, status?: number) {
    super(message)
    this.name = 'ApiError'
    this.status = status
  }
}

export async function apiGet<T>(path: string): Promise<T> {
  const url = `${API_BASE_URL}${path}`

  let response: Response
  try {
    response = await fetch(url, {
      method: 'GET',
      headers: { Accept: 'application/json' },
    })
  } catch {
    throw new ApiError(
      'Nao foi possivel conectar ao servidor. Verifique sua conexao e tente novamente.',
    )
  }

  if (!response.ok) {
    throw new ApiError(
      `A API respondeu com erro (${response.status}).`,
      response.status,
    )
  }

  return (await response.json()) as T
}
