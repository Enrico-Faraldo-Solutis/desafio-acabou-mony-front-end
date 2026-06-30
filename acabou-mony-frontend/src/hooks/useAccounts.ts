import { useQuery } from "@tanstack/react-query"
import { listAccounts, listAccountsByUser, getBalance } from "../api"

export function useAccounts(page = 0, size = 10) {
  return useQuery({
    queryKey: ["accounts", page, size],
    queryFn: () => listAccounts({ page, size }),
  })
}

export function useAccountsByUser(usuarioId: number | null, page = 0, size = 10) {
  return useQuery({
    queryKey: ["accounts", "user", usuarioId, page, size],
    queryFn: () => listAccountsByUser(usuarioId!, { page, size }),
    enabled: usuarioId !== null,
  })
}

export function useBalance(contaId: number | null) {
  return useQuery({
    queryKey: ["balance", contaId],
    queryFn: () => getBalance(contaId!),
    enabled: contaId !== null,
  })
}
