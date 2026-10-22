import { apiGet } from './http'
import type { PaginatedResponse, Produto } from '../types/produto'

export function listarProdutos(
  page = 0,
  size = 20,
): Promise<PaginatedResponse<Produto>> {
  return apiGet<PaginatedResponse<Produto>>(
    `/v1/public/produtos?page=${page}&size=${size}`,
  )
}

export function buscarProdutoPorId(id: number): Promise<Produto> {
  return apiGet<Produto>(`/v1/public/produtos/${id}`)
}
