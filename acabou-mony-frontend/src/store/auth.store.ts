import { create } from "zustand"
import { persist } from "zustand/middleware"

interface AuthState {
  token: string | null
  usuarioId: number | null
  usuarioNome: string | null
  setUsuarioId: (id: number) => void
  setUsuarioNome: (nome: string) => void
  setToken: (token: string) => void
  logout: () => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      usuarioId: null,
      usuarioNome: null,
      setUsuarioId: (id) => set({ usuarioId: id }),
      setUsuarioNome: (nome) => set({ usuarioNome: nome }),
      setToken: (token) => set({ token }),
      logout: () => {
        set({ token: null, usuarioId: null, usuarioNome: null })
        window.location.href = "/login"
      },
    }),
    { name: "auth-storage" },
  ),
)
