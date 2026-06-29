import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { getTransactionsByAccount, createTransaction, listTransactions } from "../api"
import type { TransactionCreateRequest } from "../types"

export function useTransactionsByAccount(contaId: number | null) {
  return useQuery({
    queryKey: ["transactions", "account", contaId],
    queryFn: () => getTransactionsByAccount(contaId!),
    enabled: contaId !== null,
  })
}

export function useAllTransactions() {
  return useQuery({
    queryKey: ["transactions"],
    queryFn: listTransactions,
  })
}

export function useCreateTransaction() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: TransactionCreateRequest) => createTransaction(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["transactions"] })
      queryClient.invalidateQueries({ queryKey: ["balance"] })
    },
  })
}
