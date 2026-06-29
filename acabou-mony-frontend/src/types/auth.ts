export interface LoginRequest {
  email: string
  senha: string
}

export interface LoginResponse {
  mensagem: string
  usuarioId: number
}

export interface Verify2FARequest {
  usuarioId: number
  codigo: string
}

export interface TokenResponse {
  token: string
  tipo: "Bearer"
}
