import { useState } from "react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import {
  transactionSchema,
  type TransactionFormData,
} from "../../schemas/transaction.schema"
import { useTransactionsByAccount, useCreateTransaction } from "../../hooks/useTransactions"
import { useAccounts, useAccountsByUser } from "../../hooks/useAccounts"
import { useAuthStore } from "../../store/auth.store"
import { Header } from "../../components/layout/Header"
import { Button } from "../../components/ui/button"
import { Input } from "../../components/ui/input"
import { Card, CardContent, CardHeader, CardTitle } from "../../components/ui/card"
import { Select } from "../../components/ui/select"
import { Label } from "../../components/ui/label"
import { LoadingSpinner } from "../../components/shared/LoadingSpinner"
import { CurrencyDisplay } from "../../components/shared/CurrencyDisplay"
import { StatusBadge } from "../../components/shared/StatusBadge"
import { formatDate } from "../../lib/utils"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "../../components/ui/table"

export function TransactionsPage() {
  const [selectedContaId, setSelectedContaId] = useState<number | null>(null)
  const usuarioId = useAuthStore((s) => s.usuarioId)
  const { data: accountsData } = useAccounts()
  const { data: transactions, isLoading } = useTransactionsByAccount(selectedContaId)
  const createTransactionMutation = useCreateTransaction()
  const { data: userAccountsData } = useAccountsByUser(usuarioId)

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<TransactionFormData>({
    resolver: zodResolver(transactionSchema),
  })

  function onSubmit(data: TransactionFormData) {
    createTransactionMutation.mutate(
      {
        contaOrigemId: Number(data.contaOrigemId),
        contaDestinoId: Number(data.contaDestinoId),
        valor: Number(data.valor),
        tipo: data.tipo as "DEBITO" | "CREDITO" | "TRANSFERENCIA",
      },
      {
        onSuccess: () => {
          reset()
          alert("Transação realizada com sucesso!")
        },
        onError: (error: any) => {
          alert(`Erro ao realizar transação: ${error?.response?.data?.message || error.message}`)
        },
      }
    )
  }

  const accountOptions =
    userAccountsData?.content.map((a) => ({
      value: a.id,
      label: `Conta #${a.id} - Saldo: R$ ${a.saldo.toFixed(2)}`,
    })) ?? []

  return (
    <div>
      <Header title="Transações" description="Histórico de transações" />

      <Card className="max-w-lg mx-auto mb-6">
        <CardHeader>
          <CardTitle>Nova Transação</CardTitle>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="contaOrigemId">Conta de Origem</Label>
              <select
                id="contaOrigemId"
                className="flex h-9 w-full rounded-md border border-neutral-300 bg-transparent px-3 py-1 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-neutral-400 disabled:cursor-not-allowed disabled:opacity-50"
                {...register("contaOrigemId")}
              >
                <option value="">Selecione</option>
                {accountOptions.map((opt) => (
                  <option key={opt.value} value={opt.value}>
                    {opt.label}
                  </option>
                ))}
              </select>
              {errors.contaOrigemId && (
                <p className="text-xs text-red-600">{errors.contaOrigemId.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="contaDestinoId">Conta de Destino</Label>
              <select
                id="contaDestinoId"
                className="flex h-9 w-full rounded-md border border-neutral-300 bg-transparent px-3 py-1 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-neutral-400 disabled:cursor-not-allowed disabled:opacity-50"
                {...register("contaDestinoId")}
              >
                <option value="">Selecione</option>
                {accountOptions.map((opt) => (
                  <option key={opt.value} value={opt.value}>
                    {opt.label}
                  </option>
                ))}
              </select>
              {errors.contaDestinoId && (
                <p className="text-xs text-red-600">{errors.contaDestinoId.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="tipo">Tipo</Label>
              <select
                id="tipo"
                className="flex h-9 w-full rounded-md border border-neutral-300 bg-transparent px-3 py-1 text-sm shadow-sm transition-colors focus-visible:outline-none focus-visible:ring-1 focus-visible:ring-neutral-400 disabled:cursor-not-allowed disabled:opacity-50"
                {...register("tipo")}
              >
                <option value="">Selecione</option>
                <option value="TRANSFERENCIA">Transferência</option>
                <option value="DEBITO">Débito</option>
                <option value="CREDITO">Crédito</option>
              </select>
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
                {...register("valor")}
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
              {createTransactionMutation.isPending ? "Processando..." : "Realizar Transação"}
            </Button>
          </form>
        </CardContent>
      </Card>

      <Card>
        <CardHeader>
          <CardTitle>Transações por Conta</CardTitle>
        </CardHeader>
        <CardContent>
          <div className="mb-4 max-w-xs">
            <Label htmlFor="contaFilter">Filtrar por Conta</Label>
            <Select
              id="contaFilter"
              options={
                accountsData?.content.map((a) => ({
                  value: String(a.id),
                  label: `Conta #${a.id}`,
                })) ?? []
              }
              placeholder="Selecione uma conta"
              value={selectedContaId ? String(selectedContaId) : ""}
              onChange={(e) =>
                setSelectedContaId(e.target.value ? Number(e.target.value) : null)
              }
            />
          </div>

          {isLoading ? (
            <LoadingSpinner />
          ) : transactions && transactions.length > 0 ? (
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>ID</TableHead>
                  <TableHead>Origem</TableHead>
                  <TableHead>Destino</TableHead>
                  <TableHead>Tipo</TableHead>
                  <TableHead>Valor</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead>Data</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {transactions.map((tx) => (
                  <TableRow key={tx.id}>
                    <TableCell>{tx.id}</TableCell>
                    <TableCell>{tx.contaOrigemId}</TableCell>
                    <TableCell>{tx.contaDestinoId}</TableCell>
                    <TableCell>
                      <StatusBadge type="transactionType" value={tx.tipo} />
                    </TableCell>
                    <TableCell>
                      <CurrencyDisplay value={tx.valor} />
                    </TableCell>
                    <TableCell>
                      <StatusBadge type="transaction" value={tx.status} />
                    </TableCell>
                    <TableCell>{formatDate(tx.dataTransacao)}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          ) : (
            <p className="text-sm text-neutral-500">
              {selectedContaId
                ? "Nenhuma transação encontrada para esta conta."
                : "Selecione uma conta para ver as transações."}
            </p>
          )}
        </CardContent>
      </Card>
    </div>
  )
}

