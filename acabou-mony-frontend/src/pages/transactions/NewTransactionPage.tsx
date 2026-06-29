import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import {
  transactionSchema,
  type TransactionFormData,
} from "../../schemas/transaction.schema"
import { useCreateTransaction } from "../../hooks/useTransactions"
import { useAccounts } from "../../hooks/useAccounts"
import { Header } from "../../components/layout/Header"
import { Button } from "../../components/ui/button"
import { Input } from "../../components/ui/input"
import { Label } from "../../components/ui/label"
import { Select } from "../../components/ui/select"
import { Card, CardContent, CardHeader, CardTitle } from "../../components/ui/card"
import { useNavigate } from "react-router-dom"

export function NewTransactionPage() {
  const navigate = useNavigate()
  const createTransactionMutation = useCreateTransaction()
  const { data: accountsData } = useAccounts()

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<TransactionFormData>({
    resolver: zodResolver(transactionSchema),
  })

  function onSubmit(data: TransactionFormData) {
    createTransactionMutation.mutate(data as { contaOrigemId: number; contaDestinoId: number; valor: number; tipo: "DEBITO" | "CREDITO" | "TRANSFERENCIA" }, {
      onSuccess: () => navigate("/transactions"),
    })
  }

  const accountOptions =
    accountsData?.content.map((a) => ({
      value: String(a.id),
      label: `Conta #${a.id} - Saldo: R$ ${a.saldo.toFixed(2)}`,
    })) ?? []

  return (
    <div>
      <Header title="Nova Transação" description="Criar uma nova transação financeira" />

      <Card className="max-w-lg">
        <CardHeader>
          <CardTitle>Dados da Transação</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="contaOrigemId">Conta de Origem</Label>
              <Select
                id="contaOrigemId"
                options={accountOptions}
                placeholder="Selecione"
                {...register("contaOrigemId", { valueAsNumber: true })}
              />
              {errors.contaOrigemId && (
                <p className="text-xs text-red-600">{errors.contaOrigemId.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="contaDestinoId">Conta de Destino</Label>
              <Select
                id="contaDestinoId"
                options={accountOptions}
                placeholder="Selecione"
                {...register("contaDestinoId", { valueAsNumber: true })}
              />
              {errors.contaDestinoId && (
                <p className="text-xs text-red-600">{errors.contaDestinoId.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="tipo">Tipo</Label>
              <Select
                id="tipo"
                options={[
                  { value: "TRANSFERENCIA", label: "Transferência" },
                  { value: "DEBITO", label: "Débito" },
                  { value: "CREDITO", label: "Crédito" },
                ]}
                placeholder="Selecione"
                {...register("tipo")}
              />
              {errors.tipo && (
                <p className="text-xs text-red-600">{errors.tipo.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="valor">Valor (R$)</Label>
              <Input
                id="valor"
                type="number"
                step="0.01"
                min="0.01"
                {...register("valor", { valueAsNumber: true })}
              />
              {errors.valor && (
                <p className="text-xs text-red-600">{errors.valor.message}</p>
              )}
            </div>

            {createTransactionMutation.isError && (
              <p className="text-sm text-red-600">
                {createTransactionMutation.error?.message ?? "Erro ao criar transação"}
              </p>
            )}

            <Button
              type="submit"
              className="w-full"
              disabled={createTransactionMutation.isPending}
            >
              {createTransactionMutation.isPending ? "Processando..." : "Criar Transação"}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  )
}
