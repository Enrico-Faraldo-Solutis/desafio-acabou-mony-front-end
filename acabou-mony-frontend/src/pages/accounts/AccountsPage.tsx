import { useState } from "react"
import { useAccounts } from "../../hooks/useAccounts"
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
  const { data, isLoading } = useAccounts(page)

  if (isLoading) return <LoadingSpinner />

  return (
    <div>
      <Header title="Contas" description="Gerenciamento de contas bancárias" />

      <Card>
        <CardHeader>
          <CardTitle>Todas as Contas</CardTitle>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>ID</TableHead>
                <TableHead>Usuário</TableHead>
                <TableHead>Saldo</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Criação</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {data?.content.map((account) => (
                <TableRow key={account.id}>
                  <TableCell>{account.id}</TableCell>
                  <TableCell>{account.usuarioId}</TableCell>
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

          <Pagination
            page={page}
            totalPages={data?.totalPages ?? 0}
            onPageChange={setPage}
          />
        </CardContent>
      </Card>
    </div>
  )
}
