import { apiClient } from "./client"
import type { CardCreateRequest, CardResponse } from "../types"

export async function createCard(data: CardCreateRequest): Promise<CardResponse> {
  const response = await apiClient.post<CardResponse>("/cards", data)
  return response.data
}

export async function getCardsByAccount(contaId: number): Promise<CardResponse[]> {
  const response = await apiClient.get<CardResponse[]>(`/cards/account/${contaId}`)
  return response.data
}

export async function toggleCardStatus(id: number): Promise<CardResponse> {
  const response = await apiClient.put<CardResponse>(`/cards/${id}/toggle-status`)
  return response.data
}
