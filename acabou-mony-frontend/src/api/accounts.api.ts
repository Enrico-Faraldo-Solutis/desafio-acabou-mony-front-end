import { apiClient } from "./client"
import type { AccountResponse, PageResponse } from "../types"

export async function listAccounts(params?: {
  page?: number
  size?: number
  sort?: string
}): Promise<PageResponse<AccountResponse>> {
  const response = await apiClient.get<PageResponse<AccountResponse>>("/accounts", { params })
  return response.data
}

export async function listAccountsByUser(usuarioId: number, params?: {
  page?: number
  size?: number
  sort?: string
}): Promise<PageResponse<AccountResponse>> {
  const response = await apiClient.get<PageResponse<AccountResponse>>(`/accounts/user/${usuarioId}`, { params })
  return response.data
}

export async function getBalance(contaId: number): Promise<AccountResponse> {
  const response = await apiClient.get<AccountResponse>(`/accounts/balance/${contaId}`)
  return response.data
}
