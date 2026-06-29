export interface UserCreateRequest {
  nome: string
  email: string
  cpf: string
  senha: string
}

export interface UserResponse {
  id: number
  nome: string
  email: string
  cpf: string
  dataCriacao: string
}
