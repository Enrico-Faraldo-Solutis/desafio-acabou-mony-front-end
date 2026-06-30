import { useState } from "react"
import { useAccountsByUser } from "../../hooks/useAccounts"
import { useAuthStore } from "../../store/auth.store"
import { Header } from "../../components/layout/Header"
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "../../components/ui/card"
import { CurrencyDisplay } from "../../components/shared/CurrencyDisplay"
import { StatusBadge } from "../../components/shared/StatusBadge"
import { LoadingSpinner } from "../../components/shared/LoadingSpinner"
import { Pagination } from "../../components/shared/Pagination"
import { formatDate } from "../../lib/utils"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "../../components/ui/table"

export function AccountsPage() {
  const [page, setPage] = useState(0)
  const usuarioId = useAuthStore((s) => s.usuarioId)
  const { data, isLoading } = useAccountsByUser(usuarioId, page)

  if (isLoading) return <LoadingSpinner />

  return (
    <div>
      <Header title="Minhas Contas" description="Gerenciamento das suas contas bancárias" />

      <Card>
        <CardHeader>
          <CardTitle>Minhas Contas</CardTitle>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>ID</TableHead>
                <TableHead>Saldo</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Criação</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {data?.content.map((account) => (
                <TableRow key={account.id}>
                  <TableCell>{account.id}</TableCell>
                  <TableCell>
                    <CurrencyDisplay value={account.saldo} />
                  </TableCell>
                  <TableCell>
                    <StatusBadge type="account" value={account.status} />
                  </TableCell>
                  <TableCell>{formatDate(account.dataCriacao)}</TableCell>
                </TableRow>
              ))}
            </TableBody>
          </Table>

          {data && data.totalPages > 1 && (
            <Pagination
              page={page}
              totalPages={data.totalPages}
              onPageChange={setPage}
            />
          )}
        </CardContent>
      </Card>
    </div>
  )
}
