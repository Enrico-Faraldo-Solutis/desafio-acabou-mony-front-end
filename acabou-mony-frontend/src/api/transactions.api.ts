import { apiClient } from "./client"
import type { TransactionCreateRequest, TransactionResponse } from "../types"

export async function createTransaction(
  data: TransactionCreateRequest,
): Promise<TransactionResponse> {
  const response = await apiClient.post<TransactionResponse>("/transactions", data)
  return response.data
}

export async function listTransactions(): Promise<TransactionResponse[]> {
  const response = await apiClient.get<TransactionResponse[]>("/transactions")
  return response.data
}

export async function getTransactionById(id: number): Promise<TransactionResponse> {
  const response = await apiClient.get<TransactionResponse>(`/transactions/${id}`)
  return response.data
}

export async function getTransactionsByAccount(
  contaId: number,
): Promise<TransactionResponse[]> {
  const response = await apiClient.get<TransactionResponse[]>(
    `/transactions/conta/${contaId}`,
  )
  return response.data
}
