import { z } from "zod"

const cpfRegex = /^\d{3}\.\d{3}\.\d{3}-\d{2}$|^\d{11}$/

export const registerSchema = z
  .object({
    nome: z.string().min(3, "Nome deve ter no mínimo 3 caracteres"),
    email: z.string().email("Email inválido"),
    cpf: z.string().regex(cpfRegex, "CPF inválido. Use 123.456.789-00 ou 12345678900"),
    senha: z.string().min(6, "Mínimo de 6 caracteres"),
    confirmSenha: z.string(),
  })
  .refine((data) => data.senha === data.confirmSenha, {
    message: "Senhas não conferem",
    path: ["confirmSenha"],
  })

export type RegisterFormData = z.infer<typeof registerSchema>
