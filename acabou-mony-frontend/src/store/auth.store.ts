import { create } from "zustand"
import { persist } from "zustand/middleware"

interface AuthState {
  token: string | null
  usuarioId: number | null
  setUsuarioId: (id: number) => void
  setToken: (token: string) => void
  logout: () => void
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      token: null,
      usuarioId: null,
      setUsuarioId: (id) => set({ usuarioId: id }),
      setToken: (token) => set({ token }),
      logout: () => {
        set({ token: null, usuarioId: null })
        window.location.href = "/login"
      },
    }),
    { name: "auth-storage" },
  ),
)
