import { apiClient } from "./client"
import type { LoginRequest, LoginResponse, Verify2FARequest, TokenResponse } from "../types"

export async function login(data: LoginRequest): Promise<LoginResponse> {
  const response = await apiClient.post<LoginResponse>("/auth/login", data)
  return response.data
}

export async function verify2FA(data: Verify2FARequest): Promise<TokenResponse> {
  const response = await apiClient.post<TokenResponse>("/auth/verify-2fa", data)
  return response.data
}
