import { useMutation } from "@tanstack/react-query"
import { useNavigate } from "react-router-dom"
import { login, verify2FA, getUserById } from "../api"
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
  const setUsuarioNome = useAuthStore((s) => s.setUsuarioNome)
  const usuarioId = useAuthStore((s) => s.usuarioId)
  const navigate = useNavigate()

  return useMutation({
    mutationFn: (data: Verify2FARequest) => verify2FA(data),
    onSuccess: async (response) => {
      setToken(response.token)
      
      // Buscar informações do usuário após autenticação
      if (usuarioId) {
        try {
          const user = await getUserById(usuarioId)
          setUsuarioNome(user.nome)
        } catch (error) {
          console.error("Erro ao buscar informações do usuário:", error)
        }
      }
      
      navigate("/dashboard")
    },
  })
}
