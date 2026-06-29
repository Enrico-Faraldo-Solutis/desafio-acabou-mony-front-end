import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { getCardsByAccount, createCard, toggleCardStatus } from "../api"
import type { CardCreateRequest } from "../types"

export function useCardsByAccount(contaId: number | null) {
  return useQuery({
    queryKey: ["cards", contaId],
    queryFn: () => getCardsByAccount(contaId!),
    enabled: contaId !== null,
  })
}

export function useCreateCard() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: CardCreateRequest) => createCard(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["cards"] })
    },
  })
}

export function useToggleCardStatus() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (id: number) => toggleCardStatus(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["cards"] })
    },
  })
}
