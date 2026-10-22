/**
 * Espelha ProdutoPublicoDTO do backend (GET /api/v1/public/produtos).
 * descricao, preco e unidadeMedida sao opcionais: o backend os omite do JSON
 * quando ausentes (default-property-inclusion: non_null), nunca envia null.
 */
export interface Produto {
  id: number
  nome: string
  descricao?: string
  preco?: number
  unidadeMedida?: string
  disponivel: boolean
}

export interface PageInfo {
  size: number
  number: number
  totalElements: number
  totalPages: number
}

export interface PaginatedResponse<T> {
  content: T[]
  page: PageInfo
}
