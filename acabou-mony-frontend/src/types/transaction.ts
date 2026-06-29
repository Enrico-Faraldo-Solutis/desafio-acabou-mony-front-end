export type TransactionType = "DEBITO" | "CREDITO" | "TRANSFERENCIA"
export type TransactionStatus = "PENDENTE" | "CONCLUIDA" | "FALHA"

export interface TransactionCreateRequest {
  contaOrigemId: number
  contaDestinoId: number
  valor: number
  tipo: TransactionType
}

export interface TransactionResponse {
  id: number
  contaOrigemId: number
  contaDestinoId: number
  valor: number
  status: TransactionStatus
  tipo: TransactionType
  dataTransacao: string
}
