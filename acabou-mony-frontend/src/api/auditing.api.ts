import { apiClient } from "./client"
import type { AuditLogResponse, PageResponse } from "../types"

export async function listAuditLogs(params?: {
  page?: number
  size?: number
  sort?: string
}): Promise<PageResponse<AuditLogResponse>> {
  const response = await apiClient.get<PageResponse<AuditLogResponse>>("/auditing/auditing", { params })
  return response.data
}

export async function getAuditLog(id: number): Promise<AuditLogResponse> {
  const response = await apiClient.get<AuditLogResponse>(`/auditing/auditing/${id}`)
  return response.data
}

export async function getAuditLogsByUser(
  usuarioId: number,
  params?: { page?: number; size?: number },
): Promise<PageResponse<AuditLogResponse>> {
  const response = await apiClient.get<PageResponse<AuditLogResponse>>(
    `/auditing/auditing/usuario/${usuarioId}`,
    { params },
  )
  return response.data
}

export async function getAuditLogsByAction(
  acao: string,
  params?: { page?: number; size?: number },
): Promise<PageResponse<AuditLogResponse>> {
  const response = await apiClient.get<PageResponse<AuditLogResponse>>(
    `/auditing/auditing/acao/${acao}`,
    { params },
  )
  return response.data
}

export async function getAuditLogsByEntity(
  nome: string,
  id: number,
  params?: { page?: number; size?: number },
): Promise<PageResponse<AuditLogResponse>> {
  const response = await apiClient.get<PageResponse<AuditLogResponse>>(
    "/auditing/auditing/entidade",
    { params: { nome, id, ...params } },
  )
  return response.data
}
