import { z } from "zod"

export const cardSchema = z.object({
  contaId: z.number({ message: "Conta é obrigatória" }),
  nomeImpresso: z
    .string()
    .min(2, "Nome impresso deve ter no mínimo 2 caracteres")
    .max(20, "Nome impresso deve ter no máximo 20 caracteres")
    .toUpperCase(),
})

export type CardFormData = z.infer<typeof cardSchema>
