import { useMutation } from "@tanstack/react-query"
import { useNavigate } from "react-router-dom"
import { login, verify2FA } from "../api"
import { useAuthStore } from "../store/auth.store"
import type { LoginRequest, Verify2FARequest } from "../types"

export function useLogin() {
  const setUsuarioId = useAuthStore((s) => s.setUsuarioId)
  const navigate = useNavigate()

  return useMutation({
    mutationFn: (data: LoginRequest) => login(data),
    onSuccess: (response) => {
      setUsuarioId(response.usuarioId)
      navigate("/login/2fa")
    },
  })
}

export function useVerify2FA() {
  const setToken = useAuthStore((s) => s.setToken)
  const navigate = useNavigate()

  return useMutation({
    mutationFn: (data: Verify2FARequest) => verify2FA(data),
    onSuccess: (response) => {
      setToken(response.token)
      navigate("/dashboard")
    },
  })
}
