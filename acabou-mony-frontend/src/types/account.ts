export type AccountStatus = "ATIVA" | "BLOQUEADA" | "ENCERRADA"

export interface AccountResponse {
  id: number
  usuarioId: number
  saldo: number
  status: AccountStatus
  dataCriacao: string
}

export interface UpdateBalanceRequest {
  contaId: number
  valor: number
}
