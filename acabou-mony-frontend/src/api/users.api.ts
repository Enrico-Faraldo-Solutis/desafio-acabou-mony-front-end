import { apiClient } from "./client"
import type { UserCreateRequest, UserResponse, PageResponse } from "../types"

export async function createUser(data: UserCreateRequest): Promise<UserResponse> {
  const response = await apiClient.post<UserResponse>("/accounts/users", data)
  return response.data
}

export async function listUsers(params?: {
  page?: number
  size?: number
  sort?: string
}): Promise<PageResponse<UserResponse>> {
  const response = await apiClient.get<PageResponse<UserResponse>>("/accounts/users", { params })
  return response.data
}

export async function getUserById(id: number): Promise<UserResponse> {
  const response = await apiClient.get<UserResponse>(`/accounts/users/${id}`)
  return response.data
}
