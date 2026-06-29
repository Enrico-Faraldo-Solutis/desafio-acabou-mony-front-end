import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { registerSchema, type RegisterFormData } from "../../schemas/register.schema"
import { useCreateUser } from "../../hooks/useUsers"
import { Button } from "../../components/ui/button"
import { Input } from "../../components/ui/input"
import { Label } from "../../components/ui/label"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "../../components/ui/card"
import { useNavigate } from "react-router-dom"

export function RegisterPage() {
  const navigate = useNavigate()
  const createUserMutation = useCreateUser()

  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
  })

  function onSubmit(data: RegisterFormData) {
    createUserMutation.mutate(
      { nome: data.nome, email: data.email, cpf: data.cpf, senha: data.senha },
      { onSuccess: () => navigate("/login") },
    )
  }

  return (
    <div className="flex min-h-screen items-center justify-center bg-neutral-50">
      <Card className="w-full max-w-md">
        <CardHeader className="text-center">
          <CardTitle className="text-2xl">Criar Conta</CardTitle>
          <CardDescription>Preencha os dados para se cadastrar</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
            <div className="space-y-2">
              <Label htmlFor="nome">Nome</Label>
              <Input id="nome" {...register("nome")} />
              {errors.nome && <p className="text-xs text-red-600">{errors.nome.message}</p>}
            </div>

            <div className="space-y-2">
              <Label htmlFor="email">Email</Label>
              <Input id="email" type="email" {...register("email")} />
              {errors.email && <p className="text-xs text-red-600">{errors.email.message}</p>}
            </div>

            <div className="space-y-2">
              <Label htmlFor="cpf">CPF</Label>
              <Input id="cpf" placeholder="123.456.789-00" {...register("cpf")} />
              {errors.cpf && <p className="text-xs text-red-600">{errors.cpf.message}</p>}
            </div>

            <div className="space-y-2">
              <Label htmlFor="senha">Senha</Label>
              <Input id="senha" type="password" {...register("senha")} />
              {errors.senha && <p className="text-xs text-red-600">{errors.senha.message}</p>}
            </div>

            <div className="space-y-2">
              <Label htmlFor="confirmSenha">Confirmar Senha</Label>
              <Input id="confirmSenha" type="password" {...register("confirmSenha")} />
              {errors.confirmSenha && (
                <p className="text-xs text-red-600">{errors.confirmSenha.message}</p>
              )}
            </div>

            {createUserMutation.isError && (
              <p className="text-sm text-red-600">
                {createUserMutation.error?.message ?? "Erro ao cadastrar"}
              </p>
            )}

            <Button type="submit" className="w-full" disabled={createUserMutation.isPending}>
              {createUserMutation.isPending ? "Cadastrando..." : "Cadastrar"}
            </Button>
          </form>

          <p className="mt-4 text-center text-sm text-neutral-500">
            Já tem conta?{" "}
            <a href="/login" className="text-neutral-900 underline underline-offset-4">
              Faça login
            </a>
          </p>
        </CardContent>
      </Card>
    </div>
  )
}
