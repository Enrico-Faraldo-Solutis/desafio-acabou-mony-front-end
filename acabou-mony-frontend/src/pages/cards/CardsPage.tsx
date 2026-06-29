import { useState } from "react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { cardSchema, type CardFormData } from "../../schemas/card.schema"
import { useCardsByAccount, useCreateCard, useToggleCardStatus } from "../../hooks/useCards"
import { useAccounts } from "../../hooks/useAccounts"
import { Header } from "../../components/layout/Header"
import { Button } from "../../components/ui/button"
import { Input } from "../../components/ui/input"
import { Label } from "../../components/ui/label"
import {
  Select,
} from "../../components/ui/select"
import {
  Card,
  CardContent,
  CardHeader,
  CardTitle,
} from "../../components/ui/card"
import { LoadingSpinner } from "../../components/shared/LoadingSpinner"
import { Badge } from "../../components/ui/badge"
import { formatDate } from "../../lib/utils"
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "../../components/ui/table"

export function CardsPage() {
  const [selectedContaId, setSelectedContaId] = useState<number | null>(null)
  const { data: accountsData } = useAccounts()
  const { data: cards, isLoading } = useCardsByAccount(selectedContaId)
  const createCardMutation = useCreateCard()
  const toggleStatusMutation = useToggleCardStatus()

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<CardFormData>({
    resolver: zodResolver(cardSchema),
  })

  function onCreateCard(data: CardFormData) {
    createCardMutation.mutate(data as { contaId: number; nomeImpresso: string }, { onSuccess: () => reset() })
  }

  return (
    <div>
      <Header title="Cartões" description="Gerenciamento de cartões" />

      <div className="grid gap-6 md:grid-cols-3">
        <Card className="md:col-span-2">
          <CardHeader>
            <CardTitle>Cartões da Conta</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="mb-4">
              <Label htmlFor="contaFilter">Selecionar Conta</Label>
              <Select
                id="contaFilter"
                options={
                  accountsData?.content.map((a) => ({
                    value: String(a.id),
                    label: `Conta #${a.id} - ${a.usuarioId}`,
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
            ) : cards && cards.length > 0 ? (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Número</TableHead>
                    <TableHead>Nome</TableHead>
                    <TableHead>Validade</TableHead>
                    <TableHead>Status</TableHead>
                    <TableHead>Ações</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {cards.map((card) => (
                    <TableRow key={card.id}>
                      <TableCell className="font-mono">{card.numeroCartao}</TableCell>
                      <TableCell>{card.nomeImpresso}</TableCell>
                      <TableCell>{formatDate(card.dataValidade)}</TableCell>
                      <TableCell>
                        <Badge variant={card.ativo ? "default" : "secondary"}>
                          {card.ativo ? "Ativo" : "Inativo"}
                        </Badge>
                      </TableCell>
                      <TableCell>
                        <Button
                          variant="outline"
                          size="sm"
                          onClick={() => toggleStatusMutation.mutate(card.id)}
                        >
                          {card.ativo ? "Desativar" : "Ativar"}
                        </Button>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            ) : (
              <p className="text-sm text-neutral-500">
                {selectedContaId
                  ? "Nenhum cartão encontrado para esta conta."
                  : "Selecione uma conta para ver os cartões."}
              </p>
            )}
          </CardContent>
        </Card>

        <Card>
          <CardHeader>
            <CardTitle>Novo Cartão</CardTitle>
          </CardHeader>
          <CardContent>
            <form onSubmit={handleSubmit(onCreateCard)} className="space-y-4">
              <div className="space-y-2">
                <Label htmlFor="contaId">Conta</Label>
                <Select
                  id="contaId"
                  options={
                    accountsData?.content.map((a) => ({
                      value: String(a.id),
                      label: `Conta #${a.id}`,
                    })) ?? []
                  }
                  placeholder="Selecione"
                  {...register("contaId", { valueAsNumber: true })}
                />
                {errors.contaId && (
                  <p className="text-xs text-red-600">{errors.contaId.message}</p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="nomeImpresso">Nome Impresso</Label>
                <Input id="nomeImpresso" {...register("nomeImpresso")} />
                {errors.nomeImpresso && (
                  <p className="text-xs text-red-600">{errors.nomeImpresso.message}</p>
                )}
              </div>

              <Button type="submit" className="w-full" disabled={createCardMutation.isPending}>
                {createCardMutation.isPending ? "Criando..." : "Criar Cartão"}
              </Button>
            </form>
          </CardContent>
        </Card>
      </div>
    </div>
  )
}
