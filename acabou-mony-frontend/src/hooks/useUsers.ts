import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query"
import { listUsers, createUser } from "../api"
import type { UserCreateRequest } from "../types"

export function useUsers(page = 0, size = 10) {
  return useQuery({
    queryKey: ["users", page, size],
    queryFn: () => listUsers({ page, size }),
  })
}

export function useCreateUser() {
  const queryClient = useQueryClient()

  return useMutation({
    mutationFn: (data: UserCreateRequest) => createUser(data),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["users"] })
    },
  })
}
