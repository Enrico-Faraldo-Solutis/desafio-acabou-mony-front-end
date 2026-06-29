import { useQuery } from "@tanstack/react-query"
import { listAccounts, getBalance } from "../api"

export function useAccounts(page = 0, size = 10) {
  return useQuery({
    queryKey: ["accounts", page, size],
    queryFn: () => listAccounts({ page, size }),
  })
}

export function useBalance(contaId: number | null) {
  return useQuery({
    queryKey: ["balance", contaId],
    queryFn: () => getBalance(contaId!),
    enabled: contaId !== null,
  })
}
