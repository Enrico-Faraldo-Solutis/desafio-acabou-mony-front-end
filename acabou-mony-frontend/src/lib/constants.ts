export const API_BASE_URL = "/api"

export const ACCOUNT_STATUS_MAP: Record<string, { label: string; color: string }> = {
  ATIVA: { label: "Ativa", color: "bg-green-100 text-green-800" },
  BLOQUEADA: { label: "Bloqueada", color: "bg-yellow-100 text-yellow-800" },
  ENCERRADA: { label: "Encerrada", color: "bg-red-100 text-red-800" },
}

export const TRANSACTION_STATUS_MAP: Record<string, { label: string; color: string }> = {
  PENDENTE: { label: "Pendente", color: "bg-yellow-100 text-yellow-800" },
  CONCLUIDA: { label: "Concluída", color: "bg-green-100 text-green-800" },
  FALHA: { label: "Falha", color: "bg-red-100 text-red-800" },
}

export const TRANSACTION_TYPE_MAP: Record<string, string> = {
  DEBITO: "Débito",
  CREDITO: "Crédito",
  TRANSFERENCIA: "Transferência",
}
