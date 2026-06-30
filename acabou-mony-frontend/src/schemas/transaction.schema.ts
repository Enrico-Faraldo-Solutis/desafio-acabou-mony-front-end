import { z } from "zod"

export const transactionSchema = z.object({
  contaOrigemId: z.coerce.number({ message: "Conta de origem é obrigatória" }).positive("Selecione uma conta de origem"),
  contaDestinoId: z.coerce.number({ message: "Conta de destino é obrigatória" }).positive("Selecione uma conta de destino"),
  valor: z.coerce.number().positive("Valor deve ser positivo").min(0.01, "Valor mínimo é R$ 0,01"),
  tipo: z.enum(["DEBITO", "CREDITO", "TRANSFERENCIA"], {
    message: "Tipo de transação inválido",
  }),
})

export type TransactionFormData = z.infer<typeof transactionSchema>
