import { useState } from "react"
import { useVerify2FA } from "../../hooks/useAuth"
import { useAuthStore } from "../../store/auth.store"
import { Button } from "../../components/ui/button"
import { Input } from "../../components/ui/input"
import { Label } from "../../components/ui/label"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "../../components/ui/card"

export function TwoFactorPage() {
  const usuarioId = useAuthStore((s) => s.usuarioId)
  const [codigo, setCodigo] = useState("")
  const verifyMutation = useVerify2FA()

  if (!usuarioId) {
    window.location.href = "/login"
    return null
  }

  function handleSubmit(e: React.FormEvent) {
    e.preventDefault()
    verifyMutation.mutate({ usuarioId, codigo })
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-neutral-50">
      <Card className="w-full max-w-md">
        <CardHeader className="text-center">
          <CardTitle className="text-2xl">Verificação 2FA</CardTitle>
          <CardDescription>Digite o código enviado ao seu email</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="codigo">Código de 6 dígitos</Label>
              <Input
                id="codigo"
                type="text"
                inputMode="numeric"
                maxLength={6}
                placeholder="000000"
                value={codigo}
                onChange={(e) => setCodigo(e.target.value.replace(/\D/g, ""))}
              />
            </div>

            {verifyMutation.isError && (
              <p className="text-sm text-red-600">
                {verifyMutation.error?.message ?? "Código inválido"}
              </p>
            )}

            <Button
              type="submit"
              className="w-full"
              disabled={codigo.length !== 6 || verifyMutation.isPending}
            >
              {verifyMutation.isPending ? "Verificando..." : "Verificar"}
            </Button>
          </form>
        </CardContent>
      </Card>
    </div>
  )
}
