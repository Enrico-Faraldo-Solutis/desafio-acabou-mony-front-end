import { useQuery } from "@tanstack/react-query"
import {
  listAuditLogs,
  getAuditLogsByUser,
  getAuditLogsByAction,
  getAuditLogsByEntity,
} from "../api"

export function useAuditLogs(page = 0, size = 10) {
  return useQuery({
    queryKey: ["audit-logs", page, size],
    queryFn: () => listAuditLogs({ page, size }),
  })
}

export function useAuditLogsByUser(usuarioId: number | null, page = 0, size = 10) {
  return useQuery({
    queryKey: ["audit-logs", "user", usuarioId, page, size],
    queryFn: () => getAuditLogsByUser(usuarioId!, { page, size }),
    enabled: usuarioId !== null,
  })
}

export function useAuditLogsByAction(acao: string | null, page = 0, size = 10) {
  return useQuery({
    queryKey: ["audit-logs", "action", acao, page, size],
    queryFn: () => getAuditLogsByAction(acao!, { page, size }),
    enabled: acao !== null && acao.length > 0,
  })
}

export function useAuditLogsByEntity(
  nome: string | null,
  id: number | null,
  page = 0,
  size = 10,
) {
  return useQuery({
    queryKey: ["audit-logs", "entity", nome, id, page, size],
    queryFn: () => getAuditLogsByEntity(nome!, id!, { page, size }),
    enabled: nome !== null && id !== null,
  })
}
