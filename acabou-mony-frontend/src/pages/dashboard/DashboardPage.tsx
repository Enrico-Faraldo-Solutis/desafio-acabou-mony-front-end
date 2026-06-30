import { useAuthStore } from "../../store/auth.store"
import { useBalance, useAccountsByUser } from "../../hooks/useAccounts"
import { useTransactionsByAccount } from "../../hooks/useTransactions"
import { Header } from "../../components/layout/Header"
import { Card, CardContent, CardHeader, CardTitle } from "../../components/ui/card"
import { CurrencyDisplay } from "../../components/shared/CurrencyDisplay"
import { StatusBadge } from "../../components/shared/StatusBadge"
import { LoadingSpinner } from "../../components/shared/LoadingSpinner"
import { formatDate } from "../../lib/utils"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "../../components/ui/table"

export function DashboardPage() {
  const usuarioId = useAuthStore((s) => s.usuarioId)
  const usuarioNome = useAuthStore((s) => s.usuarioNome)
  const { data: accountsData, isLoading: accountsLoading } = useAccountsByUser(usuarioId)

  const firstAccount = accountsData?.content?.[0]
  const { data: balanceData, isLoading: balanceLoading } = useBalance(firstAccount?.id ?? null)
  const { data: transactions, isLoading: transactionsLoading } = useTransactionsByAccount(
    firstAccount?.id ?? null,
  )

  if (accountsLoading || balanceLoading || transactionsLoading) {
    return <LoadingSpinner />
  }

  return (
    <div>
      <Header title="Dashboard" description="Visão geral da sua conta" />

      <div className="grid gap-6 md:grid-cols-3">
        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-neutral-500">
              Saldo Atual
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-3xl font-bold">
              {balanceData ? (
                <CurrencyDisplay value={balanceData.saldo} />
              ) : (
                "R$ 0,00"
              )}
            </div>
            {balanceData && (
              <p className="mt-1 text-xs text-neutral-500">
                Status: <StatusBadge type="account" value={balanceData.status} />
              </p>
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-neutral-500">
              Conta
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">#{firstAccount?.id ?? "-"}</div>
            <p className="mt-1 text-xs text-neutral-500">
              Usuário: {usuarioNome ?? "Carregando..."}
            </p>
          </CardContent>
        </Card>

        <Card>
          <CardHeader className="pb-2">
            <CardTitle className="text-sm font-medium text-neutral-500">
              Transações
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="text-2xl font-bold">{transactions?.length ?? 0}</div>
            <p className="mt-1 text-xs text-neutral-500">Total de transações</p>
          </CardContent>
        </Card>
      </div>

      <div className="mt-8">
        <h2 className="mb-4 text-lg font-semibold">Transações Recentes</h2>
        {transactions && transactions.length > 0 ? (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>ID</TableHead>
                <TableHead>Tipo</TableHead>
                <TableHead>Valor</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Data</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {transactions.slice(0, 5).map((tx) => (
                <TableRow key={tx.id}>
                  <TableCell>{tx.id}</TableCell>
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
          <p className="text-sm text-neutral-500">Nenhuma transação encontrada.</p>
        )}
      </div>
    </div>
  )
}
