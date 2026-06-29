import { useState } from "react"
import { useUsers } from "../../hooks/useUsers"
import { Header } from "../../components/layout/Header"
import { Card, CardContent, CardHeader, CardTitle } from "../../components/ui/card"
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

export function UsersPage() {
  const [page, setPage] = useState(0)
  const { data, isLoading } = useUsers(page)

  if (isLoading) return <LoadingSpinner />

  return (
    <div>
      <Header title="Usuários" description="Gerenciamento de usuários do sistema" />

      <Card>
        <CardHeader>
          <CardTitle>Todos os Usuários</CardTitle>
        </CardHeader>
        <CardContent>
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>ID</TableHead>
                <TableHead>Nome</TableHead>
                <TableHead>Email</TableHead>
                <TableHead>CPF</TableHead>
                <TableHead>Criação</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {data?.content.map((user) => (
                <TableRow key={user.id}>
                  <TableCell>{user.id}</TableCell>
                  <TableCell className="font-medium">{user.nome}</TableCell>
                  <TableCell>{user.email}</TableCell>
                  <TableCell>{user.cpf}</TableCell>
                  <TableCell>{formatDate(user.dataCriacao)}</TableCell>
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
