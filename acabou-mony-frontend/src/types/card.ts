export interface CardCreateRequest {
  contaId: number
  nomeImpresso: string
}

export interface CardResponse {
  id: number
  contaId: number
  numeroCartao: string
  nomeImpresso: string
  dataValidade: string
  ativo: boolean
}
