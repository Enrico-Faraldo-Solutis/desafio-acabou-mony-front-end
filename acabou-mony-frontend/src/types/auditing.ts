export interface AuditLogCreateRequest {
  usuarioId?: number
  acao: string
  entidadeNome: string
  entidadeId: number
  detalhes: Record<string, unknown>
  ipOrigem?: string
}

export interface AuditLogResponse {
  id: number
  usuarioId?: number
  acao: string
  entidadeNome: string
  entidadeId: number
  detalhes: Record<string, unknown>
  ipOrigem?: string
  timestamp: string
}
