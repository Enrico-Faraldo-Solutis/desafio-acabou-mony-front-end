import { useState } from "react"
import { useAuditLogs, useAuditLogsByUser, useAuditLogsByAction } from "../../hooks/useAuditing"
import { Header } from "../../components/layout/Header"
import { Card, CardContent, CardHeader, CardTitle } from "../../components/ui/card"
import { Input } from "../../components/ui/input"
import { Button } from "../../components/ui/button"
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

export function AuditingPage() {
  const [page, setPage] = useState(0)
  const [searchUsuarioId, setSearchUsuarioId] = useState("")
  const [searchAcao, setSearchAcao] = useState("")
  const [activeFilter, setActiveFilter] = useState<"all" | "user" | "action">("all")

  const { data: allLogs, isLoading: allLoading } = useAuditLogs(
    activeFilter === "all" ? page : 0,
  )
  const { data: userLogs, isLoading: userLoading } = useAuditLogsByUser(
    activeFilter === "user" && searchUsuarioId ? Number(searchUsuarioId) : null,
    page,
  )
  const { data: actionLogs, isLoading: actionLoading } = useAuditLogsByAction(
    activeFilter === "action" && searchAcao ? searchAcao : null,
    page,
  )

  const data =
    activeFilter === "user"
      ? userLogs
      : activeFilter === "action"
        ? actionLogs
        : allLogs
  const isLoading = allLoading || userLoading || actionLoading

  function handleUserSearch(e: React.FormEvent) {
    e.preventDefault()
    setPage(0)
    setActiveFilter("user")
  }

  function handleActionSearch(e: React.FormEvent) {
    e.preventDefault()
    setPage(0)
    setActiveFilter("action")
  }

  return (
    <div>
      <Header title="Auditoria" description="Logs de auditoria do sistema" />

      <div className="mb-6 grid gap-4 md:grid-cols-2">
        <Card>
          <CardHeader>
            <CardTitle>Filtrar por Usuário</CardTitle>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleUserSearch} className="flex gap-2">
              <Input
                type="number"
                placeholder="ID do usuário"
                value={searchUsuarioId}
                onChange={(e) => setSearchUsuarioId(e.target.value)}
              />
              <Button type="submit" variant="outline">
                Buscar
              </Button>
            </form>
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Filtrar por Ação</CardTitle>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleActionSearch} className="flex gap-2">
              <Input
                placeholder="Nome da ação"
                value={searchAcao}
                onChange={(e) => setSearchAcao(e.target.value)}
              />
              <Button type="submit" variant="outline">
                Buscar
              </Button>
            </form>
          </CardContent>
        </Card>
      </div>

      <Card>
        <CardHeader>
          <CardTitle>
            {activeFilter === "all"
              ? "Todos os Logs"
              : activeFilter === "user"
                ? `Logs do Usuário #${searchUsuarioId}`
                : `Logs da Ação "${searchAcao}"`}
          </CardTitle>
        </CardHeader>
        <CardContent>
          {activeFilter !== "all" && (
            <Button
              variant="ghost"
              size="sm"
              className="mb-4"
              onClick={() => {
                setActiveFilter("all")
                setPage(0)
              }}
            >
              Limpar filtro
            </Button>
          )}

          {isLoading ? (
            <LoadingSpinner />
          ) : data && data.content.length > 0 ? (
            <>
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>ID</TableHead>
                    <TableHead>Usuário</TableHead>
                    <TableHead>Ação</TableHead>
                    <TableHead>Entidade</TableHead>
                    <TableHead>ID Entidade</TableHead>
                    <TableHead>IP</TableHead>
                    <TableHead>Data</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {data.content.map((log) => (
                    <TableRow key={log.id}>
                      <TableCell>{log.id}</TableCell>
                      <TableCell>{log.usuarioId ?? "-"}</TableCell>
                      <TableCell className="font-mono text-xs">{log.acao}</TableCell>
                      <TableCell>{log.entidadeNome}</TableCell>
                      <TableCell>{log.entidadeId}</TableCell>
                      <TableCell className="font-mono text-xs">
                        {log.ipOrigem ?? "-"}
                      </TableCell>
                      <TableCell>{formatDate(log.timestamp)}</TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
              <Pagination
                page={page}
                totalPages={data.totalPages}
                onPageChange={setPage}
              />
            </>
          ) : (
            <p className="text-sm text-neutral-500">Nenhum log encontrado.</p>
          )}
        </CardContent>
      </Card>
    </div>
  )
}
