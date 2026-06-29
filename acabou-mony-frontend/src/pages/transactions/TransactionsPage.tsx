import { useState } from "react"
import { useTransactionsByAccount } from "../../hooks/useTransactions"
import { useAccounts } from "../../hooks/useAccounts"
import { Header } from "../../components/layout/Header"
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
  const { data: accountsData } = useAccounts()
  const { data: transactions, isLoading } = useTransactionsByAccount(selectedContaId)

  return (
    <div>
      <Header title="Transações" description="Histórico de transações" />

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
